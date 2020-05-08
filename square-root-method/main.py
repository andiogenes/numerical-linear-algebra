import argparse

import yaml


def process_square_root(_args):
    # Загрузка входных данных из YAML-файла
    with open(_args.source, 'r') as stream:
        try:
            config = yaml.safe_load(stream)
        except yaml.YAMLError as e:
            print(e)
            return

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
