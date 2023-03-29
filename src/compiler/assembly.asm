.data
	 i: .word 0
	 j: .word 2
	var2: .asciiz "true"

newline: .asciiz "\n"
.text
 main:
li $t0, 9
li $t1, 8
bge $t0, $t1, label

li $v0, 10
 syscall
label:
	li $v0, 4
	la $a0, var2
	syscall
li $v0, 4
 la $a0, newline
  syscall
 li $v0, 10
 syscall
