package com.flowDigram.FlowDigram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlockType {
    PlayPrompt("PlayPrompt"),
    PlayPromptAndTakeInput("PlayPromptAndTakeInput"),
    ConnectToAgent("ConnectToAgent"),
    ASRService("ASRService"),
    API("ApiCallService"),
    End("End")
    ;
    private final String type;
}
