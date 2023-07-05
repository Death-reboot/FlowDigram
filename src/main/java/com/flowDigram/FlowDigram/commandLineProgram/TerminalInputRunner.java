package com.flowDigram.FlowDigram.commandLineProgram;

import com.flowDigram.FlowDigram.service.FlowStatesService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;
@AllArgsConstructor
@Component
public class TerminalInputRunner implements CommandLineRunner {
    private final FlowDigramCliServise flowDigramCliServise;
    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (true){
            String flowId = scanner.nextLine();
            flowDigramCliServise.start(flowId);
        }
    }
}
