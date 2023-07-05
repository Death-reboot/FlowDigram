package com.flowDigram.FlowDigram.commandLineProgram;

import com.flowDigram.FlowDigram.BlockType;
import com.flowDigram.FlowDigram.entities.FlowStates;
import com.flowDigram.FlowDigram.service.FlowStatesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
@AllArgsConstructor
public class FlowDigramCliServise {
    private final FlowStatesService flowStatesService;

    public void start(String flowId){
        System.out.println("Hello, flow Id is : "+ flowId);
        List<FlowStates> flist = flowStatesService.findByFlowId(flowId);
        if(flist==null || flist.isEmpty()){
            System.out.println("Invalid Flow ID");
        }else {
            startLoop(flowId);
        }
    }

    private void startLoop(String flowId) {
        Scanner scanner = new Scanner(System.in);
        FlowStates current = flowStatesService.findStartStateByFlowId(flowId);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("flowId",flowId);
        while (!current.getBlockType().equals(BlockType.END.getType())){
            requestBody.put("stateName",current.getStateName());
            if(!current.getBlockType().equals(BlockType.CONDITION.getType())){
                System.out.println(current.getDescription());
                current = flowStatesService.findNextFlowcharts(requestBody);
            }else {
                for(FlowStates.ConditionValue c : current.getNextStateCondition()){
                    System.out.println("Press " +c.getValue()+" for "+c.getState()+" section");
                }
                requestBody.put("input",scanner.nextLine());
                current = flowStatesService.findNextFlowcharts(requestBody);
            }
        }
        System.out.println(current.getDescription());
    }

}
