mov r0,	#0
mov r4,#5
mov r2, #0 
loop:
	cmp r4,#0
	blt loop_exit
	mov r3,#-1
	add r2,r2,r3
	sub r4,r4,#1
b loop
loop_exit:
mov r1, r2
swi 0x11