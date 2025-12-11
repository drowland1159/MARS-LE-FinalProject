addi $t9 $zero, 1
addi $t1, $zero, 1
loadUnit $t1

addi $t2, $zero, 2
loadUnit $t2

MainLoop:
addi $t1, $zero, 1
addi $t2, $zero, 2
addi $t9, $zero, 1
StartMenu $t1
checkSelf $t1
checkSelf $t2
addi $v0, $zero, 5
syscall
move $t0, $v0
addi $t9, $zero, 1

beq $t0, $t9, sFire
addi $t9, $t9, 1
beq $t0, $t9, bFire
addi $t9, $t9, 1
beq $t0, $t9, aFire
addi $t9, $t9, 1
beq $t0, $t9, bandage
addi $t9, $t9, 1
beq $t0, $t9, armor
addi $t9, $t9, 1
beq $t0, $t9, rLoad

j endMain

sFire:
fire $t1, $t2
j endMain

bFire:
burstFire $t1, $t2
j endMain

aFire:
autoFire $t1, $t2
j endMain

bandage: 
heal $t1
j endMain

armor: 
rearmor $t1
j endMain

rLoad:
reload $t1
j endMain

endMain:
livingCheck $t2, $t8
beq $t8, $t9, exit
j MainLoop

exit:
addi $v0, $zero, 10
syscall 
