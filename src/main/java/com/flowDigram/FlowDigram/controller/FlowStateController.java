package com.flowDigram.FlowDigram.controller;

import com.flowDigram.FlowDigram.entities.FlowStates;
import com.flowDigram.FlowDigram.service.FlowStatesService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/FlowDigram")
@AllArgsConstructor
public class FlowStateController {

    private final FlowStatesService flowStatesService;
    @PostMapping("/addFlowDigram")
    public List<FlowStates> createFlowcharts(@RequestBody List<FlowStates> flowStates) {
        return flowStatesService.save(flowStates);
    }
    @GetMapping("/findFlow")
    public List<FlowStates> findFlowById(@RequestBody Map<String, Object> requestBody) {
        if (requestBody.containsKey("id")) {
            return Collections.singletonList(flowStatesService.findById(Long.parseLong(requestBody.get("id").toString())));
        }else if (requestBody.containsKey("flowId")){
            return flowStatesService.findByFlowId(requestBody.get("flowId").toString());
        } else {
            return flowStatesService.findAll();
        }
    }
    @PostMapping("/updateFlowDigram")
    public List<FlowStates> updateFlowcharts(@RequestBody List<FlowStates> flowStates) {
        return flowStatesService.update(flowStates);
    }
    @GetMapping("/findNext")
    public FlowStates findNextFlowcharts(@RequestBody Map<String, Object> requestBody) {
        return flowStatesService.findNextFlowcharts(requestBody);
    }
}
