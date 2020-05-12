def solve_by_jacobi(matrix, terms, eps, init=None):
    """
    Решает систему методом Якоби с заданной точностью eps.
    """
    end = len(matrix)

    iterations_count = 0

    if init is None:
        x = [0.0] * end
    else:
        x = [v for v in init]

    def compressor(i):
        return -sum(matrix[i][j] / matrix[i][i] * x[j] for j in range(0, end) if i != j)

    while True:
        x_k = [compressor(i) + terms[i] / matrix[i][i] for i in range(0, end)]
        iterations_count += 1

        norm = max((abs(x[i] - v)) for i, v in enumerate(x_k))
        x = x_k

        if norm <= eps:
            break

    return x, iterations_count
