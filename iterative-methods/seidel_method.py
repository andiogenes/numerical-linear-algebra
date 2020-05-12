def solve_by_seidel(matrix, terms, eps, init=None):
    """
    Решает систему методом Зейделя с заданной точностью eps.
    """
    end = len(matrix)

    iterations_count = 0

    if init is None:
        x = [0.0] * end
    else:
        x = [v for v in init]

    while True:
        x_k = [v for v in x]
        for i in range(0, end):
            s1 = sum(matrix[i][j] * x_k[j] for j in range(0, i))
            s2 = sum(matrix[i][j] * x[j] for j in range(i + 1, end))
            x_k[i] = (terms[i] - s1 - s2) / matrix[i][i]

        norm = max((abs(x[i] - v)) for i, v in enumerate(x_k))
        x = x_k

        iterations_count += 1

        if norm <= eps:
            break

    return x, iterations_count
