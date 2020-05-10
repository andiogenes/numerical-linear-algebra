import argparse

import yaml
from prettytable import PrettyTable

from jacobi_method import solve_by_jacobi
from seidel_method import solve_by_seidel


def process_iterative(_args):
    # Загрузка входных данных из YAML-файла
    with open(_args.source, 'r') as stream:
        try:
            config = yaml.safe_load(stream)
        except yaml.YAMLError as e:
            print(e)
            return

    matrix = config.get('matrix', [])
    terms = config.get('terms', [])
    eps = config.get('eps', 0.1)

    # Решение системы методами Якоби и Зейделя
    jacobi_solution, jacobi_iterations = solve_by_jacobi(matrix, terms, eps)
    seidel_solution, seidel_iterations = solve_by_seidel(matrix, terms, eps)

    # Формирование отчета для записи в файл
    report = {
        'jacobi': {
            'solution': jacobi_solution,
            'iterations': jacobi_iterations
        },
        'seidel': {
            'solution': seidel_solution,
            'iterations': seidel_iterations
        }
    }

    # Вывод таблицы с результатами в stdout
    table = PrettyTable(['Method', 'Solution', 'Iterations'])
    table.add_row(['Jacobi', jacobi_solution, jacobi_iterations])
    table.add_row(['Seidel', seidel_solution, seidel_iterations])

    print(table)

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
    __parser.set_defaults(func=process_iterative)

    return __parser


if __name__ == "__main__":
    _parser = parse_command_line()
    args = _parser.parse_args()
    try:
        args.func(args)
    except AttributeError:
        _parser.parse_args(['--help'])
