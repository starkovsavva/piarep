DEBUG = False
DEBUG_VIEW = False


def print_matrix(matrix, A, B):
    """Печатает матрицу в читаемом формате"""
    print("     ", end="")
    for ch in " " + B:
        print(f"{ch:>5}", end="")
    print()
    for i in range(len(matrix)):
        ch = " " if i == 0 else A[i - 1] if i - 1 < len(A) else " "
        print(f"{ch:>3} |", end="")
        for j in range(len(matrix[0])):
            print(f"{matrix[i][j]:5}", end="")
        print()
    print()


def initialize_dp(len_A, len_B, delete_cost, insert_cost):
    """Инициализирует матрицу DP и backtrack"""
    dp = [[float('inf')] * (len_B + 1) for _ in range(len_A + 1)]
    backtrack = [[''] * (len_B + 1) for _ in range(len_A + 1)]
    dp[0][0] = 0
    return dp, backtrack


def fill_base_cases(dp, backtrack, len_A, len_B, delete_cost, insert_cost, A, B):
    """Заполняет базовые случаи для первой строки и столбца"""
    for i in range(1, len_A + 1):
        dp[i][0] = dp[i - 1][0] + delete_cost
        backtrack[i][0] = 'D'
        if DEBUG:
            print(f"dp[{i}][0] = {dp[i][0]}, backtrack[{i}][0] = 'D'")

    for j in range(1, len_B + 1):
        dp[0][j] = dp[0][j - 1] + insert_cost
        backtrack[0][j] = 'I'
        if DEBUG:
            print(f"dp[0][{j}] = {dp[0][j]}, backtrack[0][{j}] = 'I'")

    if DEBUG_VIEW:
        print("Base cases filled:")
        print_matrix(dp, A, B)
    return dp, backtrack


def calculate_cell(dp, i, j, A, B, price):
    """Вычисляет стоимость для одной ячейки с учетом всех операций"""
    # Операция замены/совпадения
    replace_cost = price[0] if A[i - 1] != B[j - 1] else 0
    cost_replace = dp[i - 1][j - 1] + replace_cost

    # Вставка
    cost_insert = dp[i][j - 1] + price[1]

    # Удаление
    cost_delete = dp[i - 1][j] + price[2]

    # Транспозиция
    cost_transpose = float('inf')
    if i >= 2 and j >= 2:
        transpose_replace = 0
        if A[i - 1] != B[j - 2]: transpose_replace += price[0]
        if A[i - 2] != B[j - 1]: transpose_replace += price[0]
        cost_transpose = dp[i - 2][j - 2] + price[3] + transpose_replace

    # Выбор минимальной стоимости
    costs = {
        cost_replace: 'R' if replace_cost else 'M',
        cost_insert: 'I',
        cost_delete: 'D',
        cost_transpose: 'T'
    }
    min_cost = min(costs.keys())
    operation = costs[min_cost] if min_cost in costs else ''

    return min_cost, operation

def measure_price(dp):
    return dp[len(dp)-1][len(dp[0])-1]

def fill_dp_matrix(dp, backtrack, A, B, price):
    """Заполняет основную матрицу DP"""
    for i in range(1, len(dp)):
        for j in range(1, len(dp[0])):
            cost, op = calculate_cell(dp, i, j, A, B, price)
            dp[i][j] = cost
            backtrack[i][j] = op

            if DEBUG:
                print(f"Cell [{i}][{j}]: cost = {cost}, operation = {op}")

    if DEBUG_VIEW:
        print("Final DP matrix:")
        print_matrix(dp, A, B)
    return dp, backtrack


def recover_operations(backtrack, A, B):
    """Восстанавливает последовательность операций"""
    operations = []
    i, j = len(A), len(B)

    while i > 0 or j > 0:
        if i < 0 or j < 0: break

        op = backtrack[i][j]
        if not op:  # Обработка граничных случаев
            if j > 0:
                op = 'I'
            elif i > 0:
                op = 'D'

        operations.append(op)

        if op in ('M', 'R'):
            i -= 1
            j -= 1
        elif op == 'I':
            j -= 1
        elif op == 'D':
            i -= 1
        elif op == 'T':
            i -= 2
            j -= 2

    return ''.join(reversed(operations))


def main():
    replace_cost, insert_cost, delete_cost, transpose_cost = map(int, input().split())
    A = input().strip()
    B = input().strip()

    # 1. Инициализация структур данных
    dp, backtrack = initialize_dp(len(A), len(B), delete_cost, insert_cost)

    # 2. Заполнение базовых случаев
    dp, backtrack = fill_base_cases(dp, backtrack, len(A), len(B),
                                    delete_cost, insert_cost, A, B)

    # 3. Заполнение основной матрицы
    price = (replace_cost, insert_cost, delete_cost, transpose_cost)
    dp, backtrack = fill_dp_matrix(dp, backtrack, A, B, price)

    # 4. Восстановление пути
    operations = recover_operations(backtrack, A, B)

    # 5. Вывод результатов
    print(measure_price(dp))
    print(operations)
    print(A)
    print(B)


if __name__ == "__main__":
    main()