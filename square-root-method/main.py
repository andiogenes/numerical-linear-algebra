import argparse

import yaml

import square_root_method as srm


def process_square_root(_args):
    # Загрузка входных данных из YAML-файла
    with open(_args.source, 'r') as stream:
        try:
            config = yaml.safe_load(stream)
        except yaml.YAMLError as e:
            print(e)
            return

    matrix = config.get('matrix', [])
    terms = config.get('terms', [])

    # Вычисление необходимых данных
    solution = srm.solve(matrix, terms)
    determinant = srm.determinant(matrix)
    residual = srm.residual(matrix, terms, solution)
    inverse = srm.inverse(matrix)

    # Отчет для сериализации данных в файл
    report = {
        'solution': solution,
        'determinant': determinant,
        'residual': residual,
        'inverse': inverse
    }

    # Вывод вычисленных данных
    print('Решение: {}'.format(solution))
    print('Определитель: {}'.format(determinant))
    print('Невязка системы: {}'.format(residual))

    print()

    print('Обратная матрица:')
    for row in inverse:
        for element in row:
            print('{:^10}'.format(element), end='\t')
        print()
    print()

    e = [[1.0 if i == j else 0 for j in range(len(matrix))] for i in range(len(matrix))]
    inverse_matrix_residual = srm.residual_mm(matrix, inverse, e)

    print('Невязка обратной матрицы:')
    for row in inverse_matrix_residual:
        for element in row:
            print('{:^20}'.format(element), end='\t')
        print()

    # Сохранение результатов в файл
    with open(_args.dest, 'w') as stream:
        try:
            yaml.dump(report, stream)
        except yaml.YAMLError as e:
            print(e)
            return


def parse_command_line():
    __parser = argparse.ArgumentParser(description='')
    __parser.add_argument('--source', dest='source', type=str, default='assignment.yml')
    __parser.add_argument('--dest', dest='dest', type=str, default='result.yml')
    __parser.set_defaults(func=process_square_root)

    return __parser


if __name__ == "__main__":
    _parser = parse_command_line()
    args = _parser.parse_args()
    try:
        args.func(args)
    except AttributeError:
        _parser.parse_args(['--help'])
