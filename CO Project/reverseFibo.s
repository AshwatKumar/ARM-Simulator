mov r0,#0
@move 0 to register 0 to take input from file 
swi 0x6c
@take input from specified file
mov r2,	r0
@move input stored in r0 to r2 so r2 store n
mov r3,#0
@ r3 will store nth Fibonacci number
cmp r2,#1
@if n is 1 then exit the loop to calculate Fibonacci number
beq exit_fibo
mov r3,#1
cmp r2,#2
@if n is 2 then exit the loop to calculate Fibonacci number
beq exit_fibo
mov r4,#0 
@first
mov r5,#1 
@second
sub r2,r2,#2
@subtract 2 from n as we have already taken 2 starting values in series i.e 0&1
mov r6,#0
@r6 for temporary variable
loop:
	cmp r2,#0
	ble exit_fibo
	mov r6,r5
	add r5,r5,r4
	mov r4,r6
	@storing second in first number
	@and second will become first + second number
	sub r2,r2,#1
	@subtract 1 from n i.e r2
	mov r3,r5
	@r3 nth number
	b loop
exit_fibo:
mov r2,r3
@r2 contain fibo number
mov r3,#0
@r3 will  contain reverse
@loop to calculate reverse of number
loopreverse:
	@compare r2 with 0
	@if r2=0 then exit the loop
	cmp r2,#0
	beq loopexit
	mov r4,#0
	@quotient
	mov r5,r2
	@remiander in r5
	@loop to calculate remainder and quotient
	@in this loop I am subtracting 10 from reaminder until it will become smaller than 9
	@and calculating how many times I am doing it as this will be quotient
	whileloop:
		cmp r5,#10
		blt whileloopexit
		add r4,r4,#1
		sub r5,r5,#10
		b whileloop	
	whileloopexit:
	mov r6,r3
	mov r7,#10
	@reverse number=rev_num*10+ remainder
	mul r3,r6,r7
	add r3,r3,r5
	@store new quotient in r2
	mov r2,r4
	b loopreverse
loopexit:
@move reverse of nth number to r1 to print
mov r1,r3
mov r0,#1
swi 0x6b
swi 0x11




	
