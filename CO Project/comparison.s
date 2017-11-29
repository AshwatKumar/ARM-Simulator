mov r2, #10
mov r3, #20
cmp r2, r3
ble else_stub
        mov r0, #1
        mov r1, r2
        swi 0x6b
        b end_if
else_stub:
        mov r0, #1
        mov r1, r3
        swi 0x6b
end_if:
swi 0x11
