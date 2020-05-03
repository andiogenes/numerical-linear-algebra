from math import sqrt

from utils import sign


def decompose(matrix):
    end = len(matrix)

    s = [[0.0 for _ in range(0, end)] for _ in range(0, end)]
    d = [0.0 for _ in range(0, end)]

    for i in range(0, end):
        d[i] = sign(matrix[i][i] - sum([s[j][i] ** 2 * d[j] for j in range(0, i)]))
        s[i][i] = sqrt(abs(matrix[i][i] - sum([s[j][i] ** 2 * d[j] for j in range(0, i)])))

        for j in range(i + 1, end):
            s[i][j] = matrix[i][j] - sum([s[k][i] * s[k][j] * d[k] for k in range(0, i)])

    return s, d


def solve(matrix, terms):
    end = len(matrix)

    s, d = decompose(matrix)

    z = [terms[0] / s[0][0]]
    for i in range(1, end):
        z.append((terms[i] - sum([z[j] * s[j][i] for j in range(0, i)])) / s[i][i])

    y = [z[i] / d[i] for i in range(0, end)]

    x = [0.0 for _ in range(0, end)]
    x[end - 1] = y[end - 1] / s[end - 1][end - 1]

    for i in reversed(range(0, end - 1)):
        x[i] = (y[i] - sum([s[i][j] * x[j] for j in range(i + 1, end)])) / s[i][i]

    return x
