mov r0,	#0
swi 0x6c
@r4 will store input from file
mov r4,r0
@r2 will store sum
mov r2, #0 
loop:
	@compare r4 to 0
	@when value stored in r4 becomes less than 0 then exit loop
	cmp r4,#1
	blt loop_exit
	mov r0, #0
	swi 0x6c
	mov r3,r0
	@add nth number to value stored in r2
	add r2,r2,r3
	@decrease r4 by 1
	@r4 is storing number input integer need to take
	sub r4,r4,#1
b loop
loop_exit:
@intilaize r0 with 1 to print value in r1
mov r0, #1
@mover value in r2 to r1
mov r1, r2
swi 0x6b
swi 0x11
