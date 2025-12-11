package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import mars.mips.instructions.*;
import java.util.Random;

public class Module0_Proof extends CustomAssembly {
    @Override
    public String getName() { return "Module 0 Proof";}

    @Override
    public String getDescription() { return "A version of MIPS from MIPS LE that shows that " +
                                            "I watched the video and am showing understanding.";}

    // The below I copied as I understood it, and I would rather get started
    // on my actual program
    @Override
    protected void populate() {

          // put (same as addi)
      instructionList.add(
                new BasicInstruction("put $t0,$t1,12",
            	 "Assign value to register: set $t0 to ($t1 plus signed 16-bit immediate)",
                BasicInstructionFormat.I_FORMAT,
                "111111 sssss fffff tttttttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int placeholder = RegisterFile.getValue(operands[1]);
                     int value = operands[2] << 16 >> 16;
                     int result = placeholder + value;
                  // overflow on A+B detected when A and B have same sign and A+B has other sign.
                     if ((placeholder >= 0 && value >= 0 && result < 0)
                        || (placeholder < 0 && value < 0 && result >= 0))
                     {
                        throw new ProcessingException(statement,
                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                     }
                     RegisterFile.updateRegister(operands[0], result);
                     
                  }
               }));

    }
}