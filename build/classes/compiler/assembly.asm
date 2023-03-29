.data
	age: .word 4

newline: .asciiz "\n"
.text
	li $v0, 1
	lw $a0, age
	syscall
li $v0, 4
 la $a0, newline
  syscall 
