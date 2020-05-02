from gaussian_elimination import eliminate, substitute, determinant, residual, inverse_matrix

matrix = [
    [1, 0, 0],
    [0, 2, 0],
    [0, 0, 3]
]

terms = [1, 1, 1]

eliminated_matrix, eliminated_terms, swap_count = eliminate(matrix, terms)
solution = substitute(eliminated_matrix, eliminated_terms)

print(solution)
print(determinant(eliminated_matrix, swap_count))
print(residual(eliminated_matrix, terms, solution))
print(inverse_matrix(matrix))
