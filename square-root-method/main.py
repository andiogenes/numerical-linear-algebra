from square_root_method import decompose, solve

matrix = [
    [1.0, 0.0, 0.0],
    [0.0, 1.0, 0.0],
    [0.0, 0.0, 1.0]
]

terms = [1.0, 1.0, 1.0]

a, b = decompose(matrix)

print(a, b)
print(solve(matrix, terms))
