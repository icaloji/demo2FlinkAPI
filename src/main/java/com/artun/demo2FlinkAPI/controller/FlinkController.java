package com.artun.demo2FlinkAPI.controller;

import com.artun.demo2FlinkAPI.service.FlinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/flink")
public class FlinkController {

    private final FlinkService flinkService;

    @GetMapping(value = "/processTopicAndCassandra")
    public void processTopicAndCassandra() throws Exception {
        flinkService.processTopicAndCassandraService();
    }

}
