package com.flowDigram.FlowDigram.commandLineProgram;

import com.flowDigram.FlowDigram.BlockType;
import com.flowDigram.FlowDigram.entities.FlowStates;
import com.flowDigram.FlowDigram.service.FlowStatesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
@AllArgsConstructor
@Component
public class FlowDigramCliServise {
    private final FlowStatesService flowStatesService;
//    private final RestTemplate restTemplate;

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
        while (!current.getBlockType().equals(BlockType.End.getType())){
            requestBody.put("stateName",current.getStateName());
            if(current.getBlockType().equals(BlockType.PlayPrompt.getType()) ||
                    current.getBlockType().equals(BlockType.ConnectToAgent.getType()) ||
                    current.getBlockType().equals(BlockType.ASRService.getType())){
                System.out.println(current.getDescription());
                current = flowStatesService.findNextFlowcharts(requestBody);
            } else if (current.getBlockType().equals(BlockType.API.getType())) {
                String s = checkForApiRequirement(current);
            } else {
                for(FlowStates.ConditionValue c : current.getNextStateCondition()){
                    System.out.println("Press " +c.getValue()+" for "+c.getState()+" section");
                }
                requestBody.put("input",scanner.nextLine());
                current = flowStatesService.findNextFlowcharts(requestBody);
            }
        }
        System.out.println(current.getDescription());
    }

    private String checkForApiRequirement(FlowStates current) {
        String url = "http://localhost:8080/dummy/";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }

}