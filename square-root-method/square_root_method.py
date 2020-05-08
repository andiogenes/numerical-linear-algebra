from functools import reduce
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


def inverse(matrix):
    """
    Вычисляет обратную матрицу к заданной.
    """

    end = len(matrix)

    special_terms = [[0.0 for _ in range(0, end)] for _ in range(0, end)]
    for i in range(0, end):
        special_terms[i][i] = 1.0

    columns = [solve(matrix, t) for t in special_terms]

    _inverse_matrix = [[0.0 for _ in range(0, end)] for _ in range(0, end)]
    for i in range(0, end):
        for j in range(0, end):
            _inverse_matrix[j][i] = columns[i][j]

    return _inverse_matrix


def residual(matrix, terms, solution):
    """
    Вычисляет вектор невязки.
    """
    end = len(matrix)

    _residual = [0.0] * end
    for i in range(0, end):
        _residual[i] = terms[i] - sum(map(lambda it: it[1] * solution[it[0]], enumerate(matrix[i])))

    return _residual


def matrix_multiplication(first, second):
    """
    Умножает матрицы first и second.
    """
    assert len(first) > 0
    assert len(second) > 0

    result = [[0.0 for _ in range(len(second))] for _ in range(len(first))]

    for i in range(0, len(first)):
        for j in range(0, len(second[0])):
            for k in range(0, len(second)):
                result[i][j] += first[i][k] * second[k][j]

    return result


def residual_mm(first, second, expected):
    """
    Вычисляет невязку умножения матриц.
    """
    assert len(first) > 0
    assert len(second) > 0
    assert len(expected) > 0

    got = matrix_multiplication(first, second)

    _residual = [row.copy() for row in expected]

    for i in range(len(got)):
        for j in range(len(got[0])):
            _residual[i][j] -= got[i][j]

    return _residual


def determinant(matrix):
    """
    Вычисляет определитель матрицы.
    """

    s, d = decompose(matrix)

    s_det = reduce(
        lambda a, x: a * x,
        map(
            lambda it: s[it][it],
            range(0, len(s))
        ))

    d_det = reduce(lambda a, x: a * x, d)

    return s_det ** 2 * d_det
