package com.flowDigram.FlowDigram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlockType {
    START("Start"),END("End"),BLOCK("Block"),CONDITION("Condition");
    private final String type;
}
