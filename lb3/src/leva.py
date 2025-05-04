DEBUG = True
DEBUG_VIEW = False


def print_matrix(matrix, s1, s2):
    """Печатает матрицу в читаемом формате"""
    print("     ", end="")
    for ch in " " + s2:
        print(f"{ch:>5}", end="")
    print()
    for i in range(len(matrix)):
        ch = " " if i == 0 else s1[i - 1] if i - 1 < len(s1) else " "
        print(f"{ch:>3} |", end="")
        for j in range(len(matrix[0])):
            print(f"{matrix[i][j]:5}", end="")
        print()
    print()

def lev_distance(i, j, s1, s2, matrix):
    if i == 0 and j == 0:
        return 0
    elif j == 0 and i > 0:
        return i
    elif i == 0 and j > 0:
        return j
    else:
        m = 0 if s1[i - 1] == s2[j - 1] else 1
        if DEBUG:
            minn = min(matrix[i][j - 1] + 1, matrix[i - 1][j] + 1, matrix[i - 1][j - 1] + m)
            if minn == matrix[i][j-1] + 1 :
                print(f"Operation insert: matrix[{i}][{j}] = {matrix[i][j-1] + 1}")
            if minn == matrix[i-1][j] + 1:
                print(f"Operation delete: matrix[{i}][{j}] = {matrix[i-1][j] + 1}")
            if minn == matrix[i-1][j-1] + m:
                print(f"Operation match: matrix[{i}][{j}] = {matrix[i][j-1]} + m: {m}")

        return min(matrix[i][j - 1] + 1, matrix[i - 1][j] + 1, matrix[i - 1][j - 1] + m)


def calculate_levenshtein_distance(s1, s2):
    n = len(s1)
    m = len(s2)
    matrix = [[0 for i in range(m + 1)] for j in range(n + 1)]
    for i in range(n + 1):
        for j in range(m + 1):
            matrix[i][j] = lev_distance(i, j, s1, s2, matrix)
            # if DEBUG:
            #     print_matrix(matrix,s1,s2)
    return matrix[n][m]


s1 = input()
s2 = input()

print(calculate_levenshtein_distance(s1, s2))