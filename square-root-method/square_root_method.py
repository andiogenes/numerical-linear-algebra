from math import sqrt

from utils import sign


def decompose(matrix):
    """
    Раскладывает матрицу A в произведение матриц ST x D x S.

    Возвращает матрицы S и D.
    """

    end = len(matrix)

    s = [[0.0 for _ in range(0, end)] for _ in range(0, end)]
    d = [0.0 for _ in range(0, end)]

    for i in range(0, end):
        # Вычисление d_i и s_ii
        d[i] = sign(matrix[i][i] - sum(s[j][i] ** 2 * d[j] for j in range(0, i)))
        s[i][i] = sqrt(abs(matrix[i][i] - sum(s[j][i] ** 2 * d[j] for j in range(0, i))))

        # Вычисление s_ij
        for j in range(i + 1, end):
            s[i][j] = matrix[i][j] - sum(s[k][i] * s[k][j] * d[k] for k in range(0, i))
            s[i][j] /= s[i][i] * d[i]

    return s, d


def solve(matrix, terms):
    """
    Находит решение системы Ax = b методом квадратного корня.
    """

    end = len(matrix)

    # Разложение A
    s, d = decompose(matrix)

    # Решение системы STz = b
    z = [terms[0] / s[0][0]]
    for i in range(1, end):
        z.append((terms[i] - sum(z[j] * s[j][i] for j in range(0, i))) / s[i][i])

    # Решение системы Dy = z
    y = [z[i] / d[i] for i in range(0, end)]

    # Решение системы Sx = y
    x = [0.0 for _ in range(0, end)]
    x[end - 1] = y[end - 1] / s[end - 1][end - 1]

    for i in reversed(range(0, end - 1)):
        x[i] = (y[i] - sum(s[i][j] * x[j] for j in range(i + 1, end))) / s[i][i]

    return x
