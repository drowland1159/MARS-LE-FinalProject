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
addi $t3, $zero, 1

beq $t0, $t3, sFire
addi $t3, $t3, 1
beq $t0, $t3, bFire
addi $t3, $t3, 1
beq $t0, $t3, aFire
addi $t3, $t3, 1
beq $t0, $t3, bandage
addi $t3, $t3, 1
beq $t0, $t3, armor
addi $t3, $t3, 1
beq $t0, $t3, rLoad

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
