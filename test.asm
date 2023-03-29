#############################################################################
# oddEven.asm
# program written by Nathnael Dereje
#############################################################################


	.data
age: .word 3



	.text
	.globl main

main:
	
	lw $t0, age
	addi $t0, $t0, 1
	# print an integer

	li $v0, 1
	add $a0, $t0, 0
	syscall
	