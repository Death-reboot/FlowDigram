package com.flowDigram.FlowDigram.commandLineProgram;

import com.flowDigram.FlowDigram.BlockType;
import com.flowDigram.FlowDigram.entities.FlowStates;
import com.flowDigram.FlowDigram.service.FlowStatesService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
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
            if(current.getPrompt()!= null){
                System.out.println(current.getPrompt());
            }
            requestBody.put("stateName",current.getStateName());
            if(current.getBlockType().equals(BlockType.Start.getType()) ||
                    current.getBlockType().equals(BlockType.PlayPrompt.getType()) ||
                    current.getBlockType().equals(BlockType.ConnectToAgent.getType()) ||
                    current.getBlockType().equals(BlockType.ASRService.getType())){
//                System.out.println(current.getDescription());
                current = flowStatesService.findNextFlowcharts(requestBody);
            } else if (current.getBlockType().equals(BlockType.API.getType())) {
                current = checkForApiRequirement(current,requestBody);
            } else if (current.getBlockType().equals(BlockType.PlayPromptAndTakeInput.getType())){
                requestBody.put("input",scanner.nextLine());
                current = flowStatesService.findNextFlowcharts(requestBody);
            }
        }
        System.out.println(current.getDescription());
    }

    private FlowStates checkForApiRequirement(FlowStates current, Map<String, Object> requestBody) {
        Scanner scanner = new Scanner(System.in);
        RestTemplate restTemplate = new RestTemplate();

        try {
            String url = current.getNextStateCondition().get(0).getValue();

            // Make the initial GET request
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();

                // Ensure the response body is not null
                if (responseBody != null) {
                    Gson gson = new GsonBuilder().create();
                    Type responseType = new TypeToken<List<List<String>>>() {}.getType();
                    List<List<String>> data = gson.fromJson(responseBody, responseType);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    Map<String, Object> payload = new HashMap<>();

                    for (String s : data.get(0)) {
                        System.out.println(s);
                    }
                    for (String s : data.get(1)) {
                        System.out.println(s);
                        payload.put(s, scanner.nextLine());
                    }

                    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

                    // Make the POST request
                    response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );

                    // Handle the API response
                    if (response.getStatusCode().is2xxSuccessful()) {
                        responseBody = response.getBody();
                        System.out.println(responseBody);
                    } else {
                        // Handle failed cases
                    }
                } else {
                    System.out.println("Empty response body received.");
                }
            } else {
                // Handle error cases
            }
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
        } finally {
            return flowStatesService.findNextFlowcharts(requestBody);
        }
    }

}