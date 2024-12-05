package com.epam.training.ticketservice.ui.config;

import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PromptConfigTest {

    @Test
    void testGetPrompt() {
        // Arrange
        PromptConfig promptConfig = new PromptConfig();

        // Act
        AttributedString result = promptConfig.getPrompt();

        // Assert
        assertNotNull(result);
        assertEquals("Ticket service>", result.toString());
    }
}
