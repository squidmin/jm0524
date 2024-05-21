package com.toolrental.demo.service;

import com.toolrental.demo.model.Tool;
import com.toolrental.demo.repository.ToolRepository;
import com.toolrental.demo.testconfig.TestConfig;
import com.toolrental.demo.util.ApplicationConstants.ToolCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
class ToolServiceTest {

    private ToolService toolService;

    @Autowired
    private ToolRepository toolRepositoryMock;

    @BeforeEach
    void setUp() {
        toolService = new ToolService(toolRepositoryMock);
    }

    @Test
    void testFindToolByCode() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.LADW);
        when(toolRepositoryMock.findByCode(ToolCode.LADW)).thenReturn(Optional.of(tool));

        Optional<Tool> result = toolService.findToolByCode(ToolCode.LADW);
        assertTrue(result.isPresent());
        assertEquals(ToolCode.LADW, result.get().getCode());
    }

}
