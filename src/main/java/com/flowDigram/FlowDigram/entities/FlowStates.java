package com.flowDigram.FlowDigram.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "flowchart")
@Data
public class FlowStates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flowId;
    private String stateName;
    private String blockType;

    private String[] nextState;

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "next_state_condition", joinColumns = @JoinColumn(name = "flow_state_id"))
    private List<ConditionValue> nextStateCondition;

    @Embeddable
    @Data
    public static class ConditionValue {
        @Column(name = "`condition`")
        private String condition;
        private String value;
        private String state;
    }

}
