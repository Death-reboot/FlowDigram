package com.flowDigram.FlowDigram.service;

import com.flowDigram.FlowDigram.BlockType;
import com.flowDigram.FlowDigram.entities.FlowStates;
import com.flowDigram.FlowDigram.repositories.FlowStatesRepositories;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@AllArgsConstructor
@Service
public class FlowStatesService {
    private final FlowStatesRepositories flowStatesRepositories;

    public List<FlowStates> save(List<FlowStates> flowStates) {
        String uid = UUID.randomUUID().toString();
        Set<String> statesName = new HashSet<>();
        for(FlowStates flowState : flowStates){
            flowState.setFlowId(uid);
            if(statesName.contains(flowState.getStateName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Block!! Name of each block should be unique ");
            }else {
                statesName.add(flowState.getStateName());
            }
        }
        check(flowStates,statesName);
        return flowStatesRepositories.saveAll(flowStates);
    }

    public FlowStates findById(Long id) {
        return flowStatesRepositories.findById(id).orElse(new FlowStates());
    }

    public List<FlowStates> findByFlowId(String flowId) {
        return flowStatesRepositories.findByFlowId(flowId);
    }

    public List<FlowStates> findAll() {
        return flowStatesRepositories.findAll();
    }

    public List<FlowStates> update(List<FlowStates> flowStates) {
        String flowId = flowStates.get(0).getFlowId();
        List<FlowStates> updateList = new ArrayList<>();
        Set<String> statesName = new HashSet<>();
        for(FlowStates flowState : flowStates){
            FlowStates f = flowStatesRepositories.findByFlowIdAndStateName(flowState.getFlowId(),flowState.getStateName());
            if(f != null){
                f.setNextState(flowState.getNextState());
                f.setDescription(flowState.getDescription());
                f.setBlockType(flowState.getBlockType());
                updateList.add(f);
            }
            flowState.setFlowId(flowId);
            updateList.add(flowState);
            if(statesName.contains(flowState.getStateName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Block!! Name of each block should be unique ");
            }else {
                statesName.add(flowState.getStateName());
            }
        }
        check(flowStates,statesName);
        return flowStatesRepositories.saveAll(flowStates);
    }
    public void check(List<FlowStates> flowStates , Set<String> statesName){
        if (!flowStates.get(0).getBlockType().equals(BlockType.START.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please Add Start Block");
        }
        for(FlowStates f : flowStates){
            if(f.getBlockType().equals(BlockType.START.getType()) ||f.getBlockType().equals(BlockType.BLOCK.getType())){
                if(f.getNextState() == null || f.getNextState().size() != 1){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Next Block in "+f.getStateName());
                }
                if(!statesName.contains(f.getNextState().get(0))){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Next Block in "+f.getStateName()+" not found !");
                }
            }
            if(f.getBlockType().equals(BlockType.CONDITION.getType())){
                if(f.getNextState() == null || f.getNextState().size()<2){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Next Block in "+f.getStateName()+" Conditional Block Must contain 2 or more value");
                }
                if(f.getNextState().size() != f.getNextStateCondition().size()){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MissMatch in Condition and Next Stages");
                }
                for(String s :  f.getNextState()){
                    if(!statesName.contains(s)){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Next Block "+s+" in "+f.getStateName()+" not found !");
                    }
                }
            }
            if(f.getBlockType().equals(BlockType.END.getType())){
                if(!(f.getNextState() == null || f.getNextState().isEmpty())){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Next Block !! End Block Can not have Next Block");
                }
            }
        }
    }

    public FlowStates findNextFlowcharts(Map<String, Object> requestBody) {
        String flowId = requestBody.get("flowId").toString();
        String name = requestBody.get("stateName").toString();
        FlowStates current = flowStatesRepositories.findByFlowIdAndStateName(flowId,name);
        if(current.getBlockType().equals(BlockType.START.getType()) ||current.getBlockType().equals(BlockType.BLOCK.getType())){
            return flowStatesRepositories.findByFlowIdAndStateName(flowId,current.getNextState().get(0));
        } else if (current.getBlockType().equals(BlockType.END.getType())) {
            return current;
        } else if (current.getBlockType().equals(BlockType.CONDITION.getType())) {
            return runCondition(current,requestBody.get("input").toString());
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Input");
        }
    }

    private FlowStates runCondition(FlowStates current, String input) {
        FlowStates defaultValue = null;
        for (FlowStates.ConditionValue c : current.getNextStateCondition()) {
            if (c.getCondition().equals("Default")) {
                defaultValue = flowStatesRepositories.findByFlowIdAndStateName(current.getFlowId(), c.getState());
            } else if (c.getCondition().equals("=") || c.getCondition().equals("==")) {
                if (input.equals(c.getValue())) {
                    return flowStatesRepositories.findByFlowIdAndStateName(current.getFlowId(), c.getState());
                }
            } else if (isNumeric(input) && isNumeric(c.getValue())) {
                if (c.getCondition().equals(">")) {
                    if (Integer.parseInt(input) > Integer.parseInt(c.getValue()))
                        return flowStatesRepositories.findByFlowIdAndStateName(current.getFlowId(), c.getState());
                }else if (c.getCondition().equals(">=")) {
                    if (Integer.parseInt(input) >= Integer.parseInt(c.getValue()))
                        return flowStatesRepositories.findByFlowIdAndStateName(current.getFlowId(), c.getState());
                }else if (c.getCondition().equals("<")) {
                    if (Integer.parseInt(input) < Integer.parseInt(c.getValue()))
                        return flowStatesRepositories.findByFlowIdAndStateName(current.getFlowId(), c.getState());
                }else if (c.getCondition().equals("<=")) {
                    if (Integer.parseInt(input) <= Integer.parseInt(c.getValue()))
                        return flowStatesRepositories.findByFlowIdAndStateName(current.getFlowId(), c.getState());
                }
            } else if (c.getCondition().equals("lengthOf") && isNumeric(c.getValue())) {
                if (input.length() == Integer.parseInt(c.getValue()))
                    return flowStatesRepositories.findByFlowIdAndStateName(current.getFlowId(), c.getState());
            }
        }
        if(defaultValue != null){
            return defaultValue;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Input");
    }
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            int number = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
