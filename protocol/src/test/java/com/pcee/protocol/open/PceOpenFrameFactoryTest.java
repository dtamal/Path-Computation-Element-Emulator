package com.pcee.protocol.open;

import static org.assertj.core.api.Assertions.*; // Import AssertJ

import com.pcee.protocol.message.PceCommonMessageHeader;
import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PceCloseObject; // For testing wrong object type
import com.pcee.protocol.message.objectframe.impl.PceOpenObject;
import java.util.LinkedList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("PceOpenFrameFactory Tests")
class PceOpenFrameFactoryTest {

  // A dummy PceCommonMessageHeader for creating PceMessage instances
  private final PceCommonMessageHeader commonHeader =
      new PceCommonMessageHeader(1, 1); // Version 1, Type 1 (Open)

  @Nested
  @DisplayName("generateOpenFrame(PceOpenObject openObject)")
  class GenerateOpenFrameWithObjectTests {

    @Test
    void shouldGenerateOpenFrameWithValidObject() {
      PceOpenObject openObject = PceObjectFrameFactory.generatePCEPOpenObject("0", "0", 30, 120);
      PceOpenFrame openFrame = PceOpenFrameFactory.generateOpenFrame(openObject);

      assertThat(openFrame).isNotNull();
      assertThat(openFrame.getOpenObject()).isNotNull();
      assertThat(openFrame.getOpenObject()).isEqualTo(openObject);
      assertThat(openFrame.getOpenObject().getKeepAliveDecimalValue()).isEqualTo(30);
      assertThat(openFrame.getOpenObject().getDeadTimerDecimalValue()).isEqualTo(120);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenOpenObjectIsNull() {
      assertThatThrownBy(() -> PceOpenFrameFactory.generateOpenFrame(null))
          .isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("generateOpenFrame(int keepAlive, int deadTimer, String pFlag, String iFlag)")
  class GenerateOpenFrameWithParametersTests {

    @ParameterizedTest(name = "KeepAlive: {0}, DeadTimer: {1}, PFlag: {2}, IFlag: {3}")
    @CsvSource({
      "30, 120, 0, 0",
      "255, 255, 1, 1", // Max values for 8-bit fields, both flags set
      "0, 0, 0, 1", // Min values, IFlag set
      "100, 200, 1, 0" // Mixed values
    })
    void shouldGenerateOpenFrameWithValidParameters(
        int keepAlive, int deadTimer, String pFlag, String iFlag) {
      PceOpenFrame openFrame =
          PceOpenFrameFactory.generateOpenFrame(keepAlive, deadTimer, pFlag, iFlag);

      assertThat(openFrame).isNotNull();
      assertThat(openFrame.getOpenObject()).isNotNull();
      assertThat(openFrame.getOpenObject().getKeepAliveDecimalValue()).isEqualTo(keepAlive);
      assertThat(openFrame.getOpenObject().getDeadTimerDecimalValue()).isEqualTo(deadTimer);
      assertThat(openFrame.getOpenObject().getObjectHeader().getPFlagBinaryString())
          .isEqualTo(pFlag);
      assertThat(openFrame.getOpenObject().getObjectHeader().getIFlagBinaryString())
          .isEqualTo(iFlag);
    }

    @ParameterizedTest(name = "KeepAlive: {0}, DeadTimer: {1}, PFlag: {2}, IFlag: {3}")
    @CsvSource({
      "30, 120, X, 0", // Invalid PFlag
      "30, 120, 0, Y", // Invalid IFlag
      "30, 120, 2, 0", // Invalid PFlag (numeric)
      "30, 120, 0, 2" // Invalid IFlag (numeric)
    })
    void shouldGenerateOpenFrameWithInvalidFlags(
        int keepAlive, int deadTimer, String pFlag, String iFlag) {
      PceOpenFrame openFrame =
          PceOpenFrameFactory.generateOpenFrame(keepAlive, deadTimer, pFlag, iFlag);

      assertThat(openFrame).isNotNull();
      assertThat(openFrame.getOpenObject()).isNotNull();
      assertThat(openFrame.getOpenObject().getKeepAliveDecimalValue()).isEqualTo(keepAlive);
      assertThat(openFrame.getOpenObject().getDeadTimerDecimalValue()).isEqualTo(deadTimer);
      // PceObjectFrameFactory.generatePCEPOpenObject defaults invalid flags to "0"
      assertThat(openFrame.getOpenObject().getObjectHeader().getPFlagBinaryString()).isEqualTo("0");
      assertThat(openFrame.getOpenObject().getObjectHeader().getIFlagBinaryString()).isEqualTo("0");
    }

    @ParameterizedTest(name = "KeepAlive: {0}, DeadTimer: {1}, PFlag: {2}, IFlag: {3}")
    @CsvSource({
      "256, 120, 0, 120", // KeepAlive too high (max 255 for 8 bits)
      "-1, 120, 0, 120", // KeepAlive too low (min 0)
      "30, 256, 30, 0", // DeadTimer too high (max 255 for 8 bits)
      "30, -1, 30, 0", // DeadTimer too low (min 0)
      "-1, 256, 0, 0" // Both KeepAlive and DeadTimer are invalid
    })
    void shouldGenerateOpenFrameWithInvalidKeepAliveAndDeadTimer(
        int keepAlive, int deadTimer, int wantKeepAlive, int wantDeadTimer) {
      // TODO this should be an exception instead
      PceOpenFrame openFrame =
          PceOpenFrameFactory.generateOpenFrame(keepAlive, deadTimer, "0", "0");

      assertThat(openFrame).isNotNull();
      assertThat(openFrame.getOpenObject()).isNotNull();
      // PceObjectFrameFactory.generatePCEPOpenObject defaults invalid keepAlive/deadTimer to 0
      assertThat(openFrame.getOpenObject().getKeepAliveDecimalValue()).isEqualTo(wantKeepAlive);
      assertThat(openFrame.getOpenObject().getDeadTimerDecimalValue()).isEqualTo(wantDeadTimer);
      assertThat(openFrame.getOpenObject().getObjectHeader().getPFlagBinaryString()).isEqualTo("0");
      assertThat(openFrame.getOpenObject().getObjectHeader().getIFlagBinaryString()).isEqualTo("0");
    }
  }

  @Nested
  @DisplayName("getOpenFrame(PceMessage message)")
  class GetOpenFrameFromMessageTests {

    @Test
    void shouldGetOpenFrameWithValidMessage() {
      PceOpenObject openObject = PceObjectFrameFactory.generatePCEPOpenObject("0", "0", 30, 120);
      LinkedList<PceObjectFrame> objects = new LinkedList<>();
      objects.add(openObject);
      PceMessage message = new PceMessage(commonHeader, objects);

      PceOpenFrame openFrame = PceOpenFrameFactory.getOpenFrame(message);

      assertThat(openFrame).isNotNull();
      assertThat(openFrame.getOpenObject()).isNotNull();
      assertThat(openFrame.getOpenObject()).isEqualTo(openObject);
    }

    @Test
    void shouldReturnNullWhenMessageIsEmpty() {
      LinkedList<PceObjectFrame> objects = new LinkedList<>();
      PceMessage message = new PceMessage(commonHeader, objects);

      PceOpenFrame openFrame = PceOpenFrameFactory.getOpenFrame(message);

      assertThat(openFrame).isNull();
    }

    @Test
    void shouldReturnNullWhenMessageHasMultipleObjects() {
      PceOpenObject openObject = PceObjectFrameFactory.generatePCEPOpenObject("0", "0", 30, 120);
      PceCloseObject closeObject =
          PceObjectFrameFactory.generatePCEPCloseObject("0", "0", 1); // Another object type
      LinkedList<PceObjectFrame> objects = new LinkedList<>();
      objects.add(openObject);
      objects.add(closeObject);
      PceMessage message = new PceMessage(commonHeader, objects);

      PceOpenFrame openFrame = PceOpenFrameFactory.getOpenFrame(message);

      assertThat(openFrame).isNull();
    }

    @Test
    void shouldThrowClassCastExceptionWhenMessageHasWrongObjectType() {
      // Create a message with a PceCloseObject instead of PceOpenObject
      PceCloseObject closeObject = PceObjectFrameFactory.generatePCEPCloseObject("0", "0", 1);

      LinkedList<PceObjectFrame> objects = new LinkedList<>();
      objects.add(closeObject);
      PceMessage message = new PceMessage(commonHeader, objects);

      // Expect a ClassCastException because getOpenFrame explicitly casts to PceOpenObject
      assertThatThrownBy(() -> PceOpenFrameFactory.getOpenFrame(message))
          .isInstanceOf(ClassCastException.class);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenMessageIsNull() {
      assertThatThrownBy(() -> PceOpenFrameFactory.getOpenFrame(null))
          .isInstanceOf(NullPointerException.class);
    }
  }
}
