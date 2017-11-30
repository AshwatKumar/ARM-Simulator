main:
	mov r0,#1
	loop:
	add r7,r7,#1
	add r3,r3,#1
	mov r1,r3
	swi 0x6b
cmp r7,#5
bne loop
	bl fib2
	swi 0x6b
swi 0x11
fib2:
	add r3,r3,#1
	mov r1,r3
	swi 0x6b
	mov r15, r14
