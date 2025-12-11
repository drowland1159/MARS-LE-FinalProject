package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import mars.mips.instructions.*;
import java.util.HashMap;
import java.util.Random;



public class MilSimMips extends CustomAssembly {

    public static final Map<Integer, Unit> mapOfUnits = new HashMap<>();
    public static final Random random = new Random();

    @Override
    public String getName() { return "Mil Sim Mips";}

    @Override
    public String getDescription() { return "A version of MIPS from MIPS LE that shows tries to " +
                                    "make Military Simulators Easier to make.";}

    @Override
    protected void populate() {

        // STARTING :: Basic Instructions :: STARTING
        // NOTE :: For these Basic Instructions I used the definitions found in
        // /mars/mips/instructions/MipsAssembly.java

        // Equivalent to addi
        instructionList.add(
            new BasicInstruction("add $t1,$t2,$t3",
            	 "Addition with overflow : set $t1 to ($t2 plus $t3)",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100000",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[1]);
                     int add2 = RegisterFile.getValue(operands[2]);
                     int sum = add1 + add2;
                  // overflow on A+B detected when A and B have same sign and A+B has other sign.
                     if ((add1 >= 0 && add2 >= 0 && sum < 0) || (add1 < 0 && add2 < 0 && sum >= 0)) {
                        throw new ProcessingException(statement,
                            "arithmetic overflow",
                            Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION
                        );
                     }
                     RegisterFile.updateRegister(operands[0], sum);
                    }
                }
            )
        );

        // Equivalent to add
        instructionList.add(
            new BasicInstruction("addi $t1,$t2,-100",
            	 "Addition immediate with overflow : set $t1 to ($t2 plus signed 16-bit immediate)",
                BasicInstructionFormat.I_FORMAT,
                "001000 sssss fffff tttttttttttttttt",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[1]);
                        int add2 = operands[2] << 16 >> 16;
                        int sum = add1 + add2;
                        // overflow on A+B detected when A and B have same sign and A+B has other sign.
                        if ((add1 >= 0 && add2 >= 0 && sum < 0) || (add1 < 0 && add2 < 0 && sum >= 0)) {
                            throw new ProcessingException(statement, "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }   
            )
        );

        // // Equivalent to la
        // instructionList.add(
        //     new BasicInstruction("la $t0, label",
        //         "Assign value to register: set $t0 to bit 0-4 from $t1",
        //         BasicInstructionFormat.I_FORMAT,
        //         "001000 fffff 00000 tttttttttttttttt",
        //         new SimulationCode() {
        //             public void simulate(ProgramStatement statement) throws ProcessingException {
        //                 int[] operands = statement.getOperands();
        //                 RegisterFile.updateRegister(operands[0], statement.getOperand(1));
        //             }
        //         }   
        //     )
        // );

        // Equvalent to sub
        instructionList.add(
            new BasicInstruction("sub $t1,$t2,$t3",
            	 "Subtraction with overflow : set $t1 to ($t2 minus $t3)",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100010",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int sub1 = RegisterFile.getValue(operands[1]);
                        int sub2 = RegisterFile.getValue(operands[2]);
                        int dif = sub1 - sub2;
                        // overflow on A-B detected when A and B have opposite signs and A-B has B's sign
                        if ((sub1 >= 0 && sub2 < 0 && dif < 0) || (sub1 < 0 && sub2 >= 0 && dif >= 0)) {
                            throw new ProcessingException(statement, "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], dif);
                    }
                }
            )
        );

        // Equivalent to j
        instructionList.add(
            new BasicInstruction("j target", 
                "Jump unconditionally : Jump to statement at target address",
                BasicInstructionFormat.J_FORMAT,
                "000010 ffffffffffffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        Globals.instructionSet.processJump(((RegisterFile.getProgramCounter() & 0xF0000000) | (operands[0] << 2)));            
                    }
                }
            )
        );

        // Equivalent to bne
        instructionList.add(
                new BasicInstruction("bne $t1,$t2,label",
                    "Branch if not equal : Branch to statement at label's address if $t1 and $t2 are not equal",
                    BasicInstructionFormat.I_BRANCH_FORMAT,
                    "000101 fffff sssss tttttttttttttttt",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            if (RegisterFile.getValue(operands[0]) != RegisterFile.getValue(operands[1])) {
                                Globals.instructionSet.processBranch(operands[2]);
                            }
                        }
                    }
               )
        );

        // Equivalent to beq
        instructionList.add(
            new BasicInstruction("beq $t1,$t2,label",
                "Branch if equal : Branch to statement at label's address if $t1 and $t2 are equal",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000100 fffff sssss tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1])) {
                            Globals.instructionSet.processBranch(operands[2]);
                        }
                    }
                }    
            )
        );

        // Equivalent to move
        instructionList.add(
            new BasicInstruction("move $t1,$t2",
            	"Move : moves $t2 values with $t1",
                BasicInstructionFormat.R_FORMAT,
                "sssss ttttt fffff 00000 110100",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        
                        RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]));
                    }
                }
            )
        );

        // Equivalent to lb
        instructionList.add(
            new BasicInstruction("lb $t1,-100($t2)",
                "Load byte : Set $t1 to sign-extended 8-bit value from effective memory byte address",
                BasicInstructionFormat.I_FORMAT,
                "100000 ttttt fffff ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        try {
                            RegisterFile.updateRegister(operands[0],
                                Globals.memory.getByte(
                                RegisterFile.getValue(operands[2]) + (operands[1] << 16 >> 16)) << 24 >> 24);
                        } 
                            catch (AddressErrorException e)
                        {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }
            )
        );
        
        // Equivalent to slt
        instructionList.add(
            new BasicInstruction("slt $t1,$t2,$t3",
                "Set less than : If $t2 is less than $t3, then set $t1 to 1 else set $t1 to 0",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 101010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        RegisterFile.updateRegister(operands[0], 
                            (RegisterFile.getValue(operands[1]) < RegisterFile.getValue(operands[2])) ? 1 : 0);
                    }
                }
            )
        );

        instructionList.add(
            new BasicInstruction("syscall", 
                "Issue a system call : Execute the system call specified by value in $v0",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 00000 00000 001100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        Globals.instructionSet.findAndSimulateSyscall(RegisterFile.getValue(2),statement);
                    }
                }
            )
        );

        // ENDING :: Basic Instructions :: ENDING

        // STARTING :: Special Instructions :: STARTING

        // reload NOT DONE
        instructionList.add(
            new BasicInstruction("reload $t1",
                "loads object as 30 if zero or less. Will load object as 31 if 1 or greater.",
                BasicInstructionFormat.I_FORMAT,
                "001100 fffff 00000 0000000000000000",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int tVal = RegisterFile.getValue(operands[0]);
                        Unit currUnit = mapOfUnits.get(tVal);
                        int newMag = 30;
                        if (currUnit.mag >= 1) {
                            newMag++;
                        }
                        currUnit.mag = newMag;
                    }
                }   
            )
        );

        // fire NOT DONE
        instructionList.add(
            new BasicInstruction("fire $t1, $t2",
            	 "$t1 fires at $t2, If $t1 will decrease by 1. If hit $t2 will decrease.",
                BasicInstructionFormat.I_FORMAT,
                "001101 sssss fffff 0000000000000000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        int sValSource= RegisterFile.getValue(operands[0]);
                        Unit sourceUnit = mapOfUnits.get(sValSource);

                        int tValSource = RegisterFile.getValue(operands[1]);
                        Unit targetUnit = mapOfUnits.get(tValSource);

                        int dealtDamage = 20;

                        if (sourceUnit.mag == 0) {
                            SystemIO.printString("*click*\n");
                        } else {
                            SystemIO.printString("*bang*\n");
                            int hit = random.nextInt(3);
                            sourceUnit.mag--; 
                            if(hit > 0) {
                               if (targetUnit.armor > 0) {
                                    if (targetUnit.armor < dealtDamage) {
                                        dealtDamage = dealtDamage - targetUnit.armor;
                                        targetUnit.health = targetUnit.health - dealtDamage;
                                    } else {
                                        targetUnit.armor = targetUnit.armor - dealtDamage;
                                    }
                                } else {
                                    targetUnit.health = targetUnit.health - 20;
                                }
                            } else {
                                SystemIO.printString("Missed!\n");
                            }  
                        }
                    }
                }
            )
        );

        // burstFire NOT DONE
        instructionList.add(
            new BasicInstruction("burstFire $t1,$t2",
            	 "$t1 fires at $t2, If $t1 will decrease by 3 or will become 0. If hit $t2 will decrease.",
                BasicInstructionFormat.I_FORMAT,
                "001111 sssss fffff 0000000000000000",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        
                        int sValSource= RegisterFile.getValue(operands[0]);
                        Unit sourceUnit = mapOfUnits.get(sValSource);

                        int tValSource = RegisterFile.getValue(operands[1]);
                        Unit targetUnit = mapOfUnits.get(tValSource);

                        for(int i = 0; i < 3; i++) {
                            int dealtDamage = 20;
                            if (sourceUnit.mag == 0) {
                                SystemIO.printString("*click*\n");
                                return;
                            } else {
                                SystemIO.printString("*bang*\n");
                                int hit = random.nextInt(4);
                                sourceUnit.mag--; 
                                if(hit > 1) {
                                    SystemIO.printString("Hit enemy! -20\n");
                                    if (targetUnit.armor > 0) {
                                        if (targetUnit.armor < dealtDamage) {
                                            dealtDamage = dealtDamage - targetUnit.armor;
                                            targetUnit.health = targetUnit.health - dealtDamage;
                                        } else {
                                            targetUnit.armor = targetUnit.armor - dealtDamage;
                                        }
                                    } else {
                                        targetUnit.health = targetUnit.health - 20;
                                    }
                                } else {
                                    SystemIO.printString("Missed!\n");
                                }  
                            }
                        }
                    }
                }
            )
        );

        // autoFire NOT DONE
        instructionList.add (
            new BasicInstruction("autoFire $t1,$t2",
            	 "$t1 fires at $t2, If $t1 will decrease by 15 or will become 0. If hit $t2 will decrease.",
                BasicInstructionFormat.I_FORMAT,
                "010000 sssss fffff 0000000000000000",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                       
                        int sValSource= RegisterFile.getValue(operands[0]);
                        Unit sourceUnit = mapOfUnits.get(sValSource);

                        int tValSource = RegisterFile.getValue(operands[1]);
                        Unit targetUnit = mapOfUnits.get(tValSource);

                        for(int i = 0; i < 15; i++) {
                            int dealtDamage = 20;
                            if (targetUnit.health <= 0) {
                                return;
                            }
                            if (sourceUnit.mag == 0) {
                                SystemIO.printString("*click*\n");
                                return;
                            } else {
                                SystemIO.printString("*bang*\n");
                                int hit = random.nextInt(10);
                                sourceUnit.mag--;
                                if(hit > 7) {
                                    SystemIO.printString("Hit enemy! -20\n");
                                    sourceUnit.mag--;
                                    if (targetUnit.armor > 0) {
                                        if (targetUnit.armor < dealtDamage) {
                                            dealtDamage = dealtDamage - targetUnit.armor;
                                            targetUnit.health = targetUnit.health - dealtDamage;
                                        } else {
                                            targetUnit.armor = targetUnit.armor - dealtDamage;
                                        }
                                    } else {
                                        targetUnit.health = targetUnit.health - 20;
                                    }
                                } else {
                                    SystemIO.printString("Missed!\n");
                                }  
                            }
                        }
                    }
                }
            )
        );

        // heal NOT DONE
        instructionList.add(
            new BasicInstruction("heal $t1",
                "Increases health by 25",
                BasicInstructionFormat.I_FORMAT,
                "010001 00000 fffff 0000000000000000",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int sValSource= RegisterFile.getValue(operands[0]);
                        Unit sourceUnit = mapOfUnits.get(sValSource);
                        sourceUnit.health = sourceUnit.health + 25;
                        if(sourceUnit.health > 100) {
                            sourceUnit.health = 100;
                        }
                    }
                }   
            )
        );

        // rearmor NOT DONE
        instructionList.add(
            new BasicInstruction("rearmor $t1",
                "Increases armor by 25",
                BasicInstructionFormat.I_FORMAT,
                "010111 00000 fffff 0000000000000000",
                new SimulationCode() {
                   public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int sValSource= RegisterFile.getValue(operands[0]);
                        Unit sourceUnit = mapOfUnits.get(sValSource);
                        sourceUnit.armor = sourceUnit.armor + 25;
                        if(sourceUnit.armor > 50) {
                            sourceUnit.armor = 50;
                        }
                    }
                }   
            )
        );

        // NOTE :: possibly outdated. Might update.
        // instructionList.add(
        //     new BasicInstruction("retreat $t1, $t2",
        //         "$t1 retreat's away from $t2, resetting $t2's value",
        //         BasicInstructionFormat.I_FORMAT,
        //         "010010 sssss fffff tttttttttttttttt",
        //         new SimulationCode() {
        //             public void simulate(ProgramStatement statement) throws ProcessingException {
        //                 int[] operands = statement.getOperands();
        //                 RegisterFile.updateRegister(operands[1], 0);
        //             }
        //         }
        //     )
        // );

        instructionList.add(
            new BasicInstruction("loadUnit $t1",
                "associates a Unit object to $t1 to access Unit functions",
                BasicInstructionFormat.I_FORMAT,
                "010011 00000 fffff 0000000000000000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        Unit newUnit = new Unit();
                        mapOfUnits.put(RegisterFile.getValue(operands[0]), newUnit);
                    }
                }
            )
        );

        instructionList.add(
            new BasicInstruction("checkSelf $t1",
                "checks how the stats of a unit $t1 has",
                BasicInstructionFormat.I_FORMAT,
                "010100 00000 fffff 0000000000000000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int tVal = RegisterFile.getValue(operands[0]);
                        Unit currUnit = mapOfUnits.get(tVal);
                        if(currUnit == null) {
                            SystemIO.printString("ERROR\n");
                            return;
                        }
                        SystemIO.printString("Player: " + tVal + " HP: " + currUnit.health 
                            + " Armor: " + currUnit.armor + " Magazine: " + currUnit.mag + "\n");
                    }
                }
            )
        );
   
        // NOTE :: All of the below would not be in the theoretical final product
        // NOTE :: I am unsure on how to print through the .data with this as my la is off
        instructionList.add(
            new BasicInstruction("StartMenu $t1",
                "checks how the stats of a unit $t1 has",
                BasicInstructionFormat.I_FORMAT,
                "010101 00000 fffff 0000000000000000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        SystemIO.printString("Your Turn:\n1. Single Fire  2. Burst Fire  3. Auto Fire" +
                            " 4. Bandage  5. Rearmor  6. Reload\n");
                    }
                }
            )
        );

        instructionList.add(
            new BasicInstruction("livingCheck $t1, $t2",
                "Checks if $t1 is alive. If so then $t2 will become 1, if not then 0",
                BasicInstructionFormat.I_FORMAT,
                "011000 sssss fffff 0000000000000000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int sValSource= RegisterFile.getValue(operands[0]);
                        Unit sourceUnit = mapOfUnits.get(sValSource);
                        if(sourceUnit.isDead()) {
                            SystemIO.printString("Player " + RegisterFile.getValue(operands[0]) + " is out of health.");
                            RegisterFile.updateRegister(operands[1], 1);
                        } else {
                            RegisterFile.updateRegister(operands[1], 0);
                        }
                    }
                }
            )
        );
        // ENDING :: Special Instructions :: ENDING
    }
}

// STARTING :: Syscalls :: STARTING
// NOTE :: I am using the syscalls found in the syscall folder
class SyscallExit extends AbstractSyscall {
    public SyscallExit() { super(10, "Exit"); }

    public void simulate(ProgramStatement statement) throws ProcessingException {
        throw new ProcessingException();  // empty exception list.
    }
}
// ENDING :: Syscalls :: ENDING

class Unit {
    public int mag;
    public int health;
    public int armor;
    // public int score;

    public Unit() {
        mag = 30;
        health = 100;
        armor = 50;
        //score = 0;
    }

    public boolean isDead() {
        if (health > 0) {
            return false;
        } else {
            return true;
        }
    }
}