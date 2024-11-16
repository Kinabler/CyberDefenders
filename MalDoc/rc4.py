def rc4(key, data):
    # KSA: Key-Scheduling Algorithm
    S = list(range(256))
    j = 0
    for i in range(256):
        j = (j + S[i] + key[i % len(key)]) % 256
        S[i], S[j] = S[j], S[i]

    # PRGA: Pseudo-Random Generation Algorithm
    i = 0
    j = 0
    keystream = []
    for byte in data:
        i = (i + 1) % 256
        j = (j + S[i]) % 256
        S[i], S[j] = S[j], S[i]
        keystream.append(S[(S[i] + S[j]) % 256])

    # XOR keystream với dữ liệu
    return bytes([data[i] ^ keystream[i] for i in range(len(data))])

# Test RC4
key = b"secret"
data = b"Hello, World!"
encrypted = rc4(key, data)
decrypted = rc4(key, encrypted)

print("Plaintext: ", data)
print("Encrypted: ", encrypted)
print("Decrypted: ", decrypted)
