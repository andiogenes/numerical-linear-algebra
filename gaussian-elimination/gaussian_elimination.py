from functools import reduce

from utils import is_zero


def eliminate(_matrix, _terms, use_pivot=True):
    """
    Прямой ход метода Гаусса.
    Возвращает обновленную матрицу, список свободных членов, количество обменов строк.
    Если use_pivot == True, используется _выбор главного элемента_.
    """
    matrix = [row.copy() for row in _matrix]
    terms = _terms.copy()
    swap_count = 0

    end = len(matrix)

    for i in range(0, end - 1):
        max_index = i

        if use_pivot:
            # Поиск опорного элемента
            for j in range(i, end):
                if abs(matrix[j][i]) > abs(matrix[max_index][i]):
                    max_index = j

            # Если все элементы в столбце ниже диагонали == 0 => матрица вырожденная
            if is_zero(matrix[max_index][i]):
                raise RuntimeError('Matrix is degenerate')

        if i != max_index:
            # Перестановка строк
            matrix[i], matrix[max_index] = matrix[max_index], matrix[i]
            terms[i], terms[max_index] = terms[max_index], terms[i]
            swap_count += 1

        for j in range(i + 1, end):
            # Устранение элементов ниже [i,i]
            c = -matrix[j][i] / matrix[i][i]
            matrix[j] = [v + matrix[k][i] * c for k, v in enumerate(matrix[j])]
            terms[j] += terms[i] * c

    return matrix, terms, swap_count


def substitute(matrix, terms):
    """
    Обратный ход метода Гаусса.
    Возвращает список чисел - решение системы.
    """
    end = len(matrix)

    solution = [0.0] * end
    for i in reversed(range(0, end)):
        ready_solution = sum(
            map(
                lambda it: matrix[i][it] * solution[it],
                range(i + 1, end - 1)
            )
        )
        solution[i] = (terms[i] - ready_solution) / matrix[i][i]

    return solution


def determinant(matrix, swap_count):
    """
    Вычисляет определитель матрицы.
    :param matrix: Исходная матрица
    :param swap_count: Количество перестановок строк
    """
    _determinant = reduce(
        lambda acc, d: acc * d,
        map(
            lambda it: matrix[it][it],
            range(0, len(matrix))
        )
    )

    return _determinant if swap_count % 2 == 0 else -_determinant


def residual(matrix, terms, solution):
    """
    Вычисляет вектор невязки.
    """
    end = len(matrix)

    _residual = [0.0] * end
    for i in range(0, end):
        _residual[i] = terms[i] - sum(map(lambda it: it[1] * solution[it[0]], enumerate(matrix[i])))

    return _residual


def inverse_matrix(matrix):
    """
    Вычисляет обратную матрицу к заданной.
    """
    end = len(matrix)

    special_terms = [[0.0 for _ in range(0, end)] for _ in range(0, end)]
    for i in range(0, end):
        special_terms[i][i] = 1.0

    def solve(_matrix, _terms):
        eliminated_matrix, eliminated_terms, _ = eliminate(_matrix, _terms)
        return substitute(eliminated_matrix, eliminated_terms)

    columns = [solve(matrix, t) for t in special_terms]

    _inverse_matrix = [[0.0 for _ in range(0, end)] for _ in range(0, end)]
    for i in range(0, end):
        for j in range(0, end):
            _inverse_matrix[j][i] = columns[i][j]

    return _inverse_matrix


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
