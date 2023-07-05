package com.flowDigram.FlowDigram.repositories;

import com.flowDigram.FlowDigram.entities.FlowStates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowStatesRepositories extends JpaRepository<FlowStates, Long> {
    List<FlowStates> findByFlowId(String flowId);

    FlowStates findByFlowIdAndStateName(String flowId, String stateName);

    FlowStates findByFlowIdAndBlockType(String flowId, String type);
}
