package com.pcee.protocol.close;

import static org.assertj.core.api.Assertions.*; // Import AssertJ

import com.pcee.protocol.message.PceCommonMessageHeader;
import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PceCloseObject;
import com.pcee.protocol.message.objectframe.impl.PceOpenObject; // For testing wrong object type
import java.util.LinkedList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("PceCloseFrameFactory Tests")
class PceCloseFrameFactoryTest {

  // A dummy PceCommonMessageHeader for creating PceMessage instances
  private final PceCommonMessageHeader commonHeader =
      new PceCommonMessageHeader(1, 1); // Version 1, Type 1 (Open)

  @Nested
  @DisplayName("generateCloseFrame(PceCloseObject closeObject)")
  class GenerateCloseFrameWithObjectTests {

    @Test
    void shouldGenerateCloseFrameWithValidObject() {
      PceCloseObject closeObject = PceObjectFrameFactory.generatePCEPCloseObject("0", "0", 1);
      PceCloseFrame closeFrame = PceCloseFrameFactory.generateCloseFrame(closeObject);

      assertThat(closeFrame).isNotNull();
      assertThat(closeFrame.getCloseObject()).isNotNull();
      assertThat(closeFrame.getCloseObject()).isEqualTo(closeObject);
      assertThat(closeFrame.getCloseObject().getReasonDecimalValue()).isEqualTo(1);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCloseObjectIsNull() {
      // The constructor of PceCloseFrame does not handle null PceCloseObject gracefully,
      // which is acceptable for a factory method that expects valid input.
      assertThatThrownBy(() -> PceCloseFrameFactory.generateCloseFrame(null))
          .isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("generateCloseFrame(int reason, String pFlag, String iFlag)")
  class GenerateCloseFrameWithParametersTests {

    @ParameterizedTest(name = "Reason: {0}, PFlag: {1}, IFlag: {2}")
    @CsvSource({
      "1, 0, 0",
      "255, 1, 1", // Max reason, both flags set
      "0, 0, 0, 1", // Min reason, IFlag set
      "100, 1, 0" // Mixed values
    })
    void shouldGenerateCloseFrameWithValidParameters(int reason, String pFlag, String iFlag) {
      PceCloseFrame closeFrame = PceCloseFrameFactory.generateCloseFrame(reason, pFlag, iFlag);

      assertThat(closeFrame).isNotNull();
      assertThat(closeFrame.getCloseObject()).isNotNull();
      assertThat(closeFrame.getCloseObject().getReasonDecimalValue()).isEqualTo(reason);
      assertThat(closeFrame.getCloseObject().getObjectHeader().getPFlagBinaryString())
          .isEqualTo(pFlag);
      assertThat(closeFrame.getCloseObject().getObjectHeader().getIFlagBinaryString())
          .isEqualTo(iFlag);
    }

    @ParameterizedTest(name = "Reason: {0}, PFlag: {1}, IFlag: {2}")
    @CsvSource({
      "1, X, 0", // Invalid PFlag
      "1, 0, Y", // Invalid IFlag
      "1, 2, 0", // Invalid PFlag (numeric)
      "1, 0, 2" // Invalid IFlag (numeric)
    })
    void shouldGenerateCloseFrameWithInvalidFlags(int reason, String pFlag, String iFlag) {
      PceCloseFrame closeFrame = PceCloseFrameFactory.generateCloseFrame(reason, pFlag, iFlag);

      assertThat(closeFrame).isNotNull();
      assertThat(closeFrame.getCloseObject()).isNotNull();
      assertThat(closeFrame.getCloseObject().getReasonDecimalValue()).isEqualTo(reason);
      // PceObjectFrameFactory.generatePCEPCloseObject defaults invalid flags to "0"
      assertThat(closeFrame.getCloseObject().getObjectHeader().getPFlagBinaryString())
          .isEqualTo("0");
      assertThat(closeFrame.getCloseObject().getObjectHeader().getIFlagBinaryString())
          .isEqualTo("0");
    }

    @ParameterizedTest(name = "Reason: {0}, PFlag: {1}, IFlag: {2}")
    @CsvSource({
      "256, 0, 0", // Reason too high (max 255 for 8 bits)
      "-1, 0, 0" // Reason too low (min 0)
    })
    void shouldGenerateCloseFrameWithInvalidReason(int reason, String pFlag, String iFlag) {
      PceCloseFrame closeFrame = PceCloseFrameFactory.generateCloseFrame(reason, pFlag, iFlag);

      assertThat(closeFrame).isNotNull();
      assertThat(closeFrame.getCloseObject()).isNotNull();
      // PceObjectFrameFactory.generatePCEPCloseObject defaults invalid reason to 0
      assertThat(closeFrame.getCloseObject().getReasonDecimalValue()).isEqualTo(0);
      assertThat(closeFrame.getCloseObject().getObjectHeader().getPFlagBinaryString())
          .isEqualTo(pFlag);
      assertThat(closeFrame.getCloseObject().getObjectHeader().getIFlagBinaryString())
          .isEqualTo(iFlag);
    }
  }

  @Nested
  @DisplayName("getCloseFrame(PceMessage message)")
  class GetCloseFrameFromMessageTests {

    @Test
    void shouldGetCloseFrameWithValidMessage() {
      PceCloseObject closeObject = PceObjectFrameFactory.generatePCEPCloseObject("0", "0", 1);
      LinkedList<PceObjectFrame> objects = new LinkedList<>();
      objects.add(closeObject);
      PceMessage message = new PceMessage(commonHeader, objects);

      PceCloseFrame closeFrame = PceCloseFrameFactory.getCloseFrame(message);

      assertThat(closeFrame).isNotNull();
      assertThat(closeFrame.getCloseObject()).isNotNull();
      assertThat(closeFrame.getCloseObject()).isEqualTo(closeObject);
    }

    @Test
    void shouldReturnNullWhenMessageIsEmpty() {
      LinkedList<PceObjectFrame> objects = new LinkedList<>();
      PceMessage message = new PceMessage(commonHeader, objects);

      PceCloseFrame closeFrame = PceCloseFrameFactory.getCloseFrame(message);

      assertThat(closeFrame).isNull();
    }

    @Test
    void shouldReturnNullWhenMessageHasMultipleObjects() {
      PceCloseObject closeObject = PceObjectFrameFactory.generatePCEPCloseObject("0", "0", 1);
      PceOpenObject openObject =
          PceObjectFrameFactory.generatePCEPOpenObject("0", "0", 30, 120); // Another object type
      LinkedList<PceObjectFrame> objects = new LinkedList<>();
      objects.add(closeObject);
      objects.add(openObject);
      PceMessage message = new PceMessage(commonHeader, objects);

      PceCloseFrame closeFrame = PceCloseFrameFactory.getCloseFrame(message);

      assertThat(closeFrame).isNull();
    }

    @Test
    void shouldThrowClassCastExceptionWhenMessageHasWrongObjectType() {
      PceOpenObject openObject =
          PceObjectFrameFactory.generatePCEPOpenObject("0", "0", 30, 120); // Not a PceCloseObject
      LinkedList<PceObjectFrame> objects = new LinkedList<>();
      objects.add(openObject);
      PceMessage message = new PceMessage(commonHeader, objects);

      // Expect a ClassCastException because getCloseFrame explicitly casts to PceCloseObject
      assertThatThrownBy(() -> PceCloseFrameFactory.getCloseFrame(message))
          .isInstanceOf(ClassCastException.class);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenMessageIsNull() {
      assertThatThrownBy(() -> PceCloseFrameFactory.getCloseFrame(null))
          .isInstanceOf(NullPointerException.class);
    }
  }
}
