package com.flowDigram.FlowDigram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlockType {
    PlayPrompt("PlayPrompt"), End("End"),BLOCK("Block"), PlayPromptAndTakeInput("PlayPromptAndTakeInput");
    private final String type;
}
