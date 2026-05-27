package com.pcee.protocol.keepalive;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PceKeepaliveFrameFactory Tests")
class PceKeepaliveFrameFactoryTest {

  @Test
  @DisplayName("Should correctly generate a PceKeepaliveFrame")
  void testGenerateKeepaliveFrame() {
    PceKeepaliveFrame keepaliveFrame = PceKeepaliveFrameFactory.generateKeepaliveFrame();

    assertNotNull(keepaliveFrame, "Generated PceKeepaliveFrame should not be null");
    assertEquals(2, keepaliveFrame.getMessageType(), "Message type should be 2 for KeepaliveFrame");
    assertEquals(0, keepaliveFrame.getByteLength(), "Byte length should be 0 for KeepaliveFrame");
    assertTrue(
        keepaliveFrame.getBinaryString().isEmpty(),
        "Binary string should be empty for KeepaliveFrame");
    assertNotNull(
        keepaliveFrame.getObjectFrameLinkedList(),
        "Object frame list should not be null for KeepaliveFrame");
    assertTrue(
        keepaliveFrame.getObjectFrameLinkedList().isEmpty(),
        "Object frame list should be empty for KeepaliveFrame");
  }
}
