from collections import deque, Counter

DEBUG = True  # включить True для отладки


class Node:
    __slots__ = ['children', 'parent', 'charToParent', 'go', 'suffLink', 'up', 'terminals']

    def __init__(self):
        self.children = {}
        self.parent = None
        self.charToParent = None
        self.go = {}
        self.suffLink = None
        self.up = None
        self.terminals = []


class AhoCorasick:
    def __init__(self):
        self.root = Node()
        self.root.parent = self.root
        self.root.suffLink = self.root
        self.root.up = self.root
        if DEBUG:
            print("Инициализирован корневой узел автомата.")

    # добавление паттерна через children
    def add_pattern(self, pattern, index):
        if DEBUG:
            print(f"\nДобавление шаблона {index}: '{pattern}'")
        node = self.root
        for char in pattern:
            if char not in node.children:
                new_node = Node()
                new_node.parent = node
                new_node.charToParent = char
                node.children[char] = new_node
            node = node.children[char]
        node.terminals.append(index)

    # просчет суффлинки
    def get_sufflink(self, node):
        if node.suffLink is not None:
            return node.suffLink

        if node.parent == self.root:
            node.suffLink = self.root
        else:
            # рекурсивная связь между get_sufflink и get_go
            parent_sufflink = self.get_sufflink(node.parent)
            node.suffLink = self.get_go(parent_sufflink, node.charToParent)

        return node.suffLink

    # переход по автомату
    def get_go(self, node, c):
        if c in node.go:
            return node.go[c]

        if c in node.children:
            node.go[c] = node.children[c]
        elif node == self.root:
            node.go[c] = self.root
        else:
            # рекурсивная связь между get_sufflink и get_go
            sufflink = self.get_sufflink(node)
            node.go[c] = self.get_go(sufflink, c)

        return node.go[c]

    # просчет терминальных ссылок
    def get_up(self, node):
        if node.up is not None:
            return node.up

        sufflink = self.get_sufflink(node)
        if sufflink == self.root or sufflink.terminals:
            node.up = sufflink
        else:
            node.up = self.get_up(sufflink)

        return node.up

    # поиск
    def search(self, text, pattern_lengths):
        result = []
        node = self.root
        for i, char in enumerate(text):
            node = self.get_go(node, char) # переход по автомату
            v = node
            while v != self.root:
                # если нашли терминал надо их всех добавить
                if v.terminals:
                    for pattern_index in v.terminals:
                        # просчет позиции в тексте
                        position = i - pattern_lengths[pattern_index] + 2
                        result.append((position, pattern_index))
                # идем по терминальной ссылке
                v = self.get_up(v)
        return result

    # нахождение самой длинной суффиксной ссылки bfs
    def max_sufflink(self):
        max_length = 0
        queue = deque()
        queue.append(self.root)
        while queue:
            node = queue.popleft()
            length = 0
            v = node
            while v != self.root:
                v = self.get_sufflink(v)
                length += 1
            max_length = max(max_length, length)
            for child in node.children.values():
                queue.append(child)
        return max_length


    # нахождение самой длинной терминальной ссылки bfs
    def max_uplink(self):
        max_len = 0
        queue = deque()
        queue.append(self.root)
        while queue:
            node = queue.popleft()
            length = 0
            v = node
            while v != self.root:
                v = self.get_up(v)
                length+=1
            max_len = max(max_len, length)
            for child in node.children.values():
                queue.append(child)

        return max_len


def wildcard(text, pattern, joker):
    n = len(text)
    m = len(pattern)
    parts = []

    aho = AhoCorasick()
    pattern_lengths = {}

    # разбиваем шаблон на подстроки без джокера и запоминаем их позиции в шаблоне
    current = ""
    for i, c in enumerate(pattern):
        if c == joker:
            if current:
                parts.append((current, i - len(current)))
                current = ""
        else:
            current += c
    if current:
        parts.append((current, m - len(current)))

    if not parts:
        return

    # добавляем части в автомат
    for pat_index, (subpattern, pos_in_pattern) in enumerate(parts, 1):
        pattern_lengths[pat_index] = len(subpattern)
        aho.add_pattern(subpattern, pat_index)

    # поиск всех вхождений подстрок
    matches = aho.search(text, pattern_lengths)
    matches.sort()

    # для каждой позиции начала текста считаем количество совпавших частей шаблона
    count = Counter()
    for position, pattern_index in matches:
        pos_in_pattern = parts[pattern_index - 1][1]
        start_pos = position - pos_in_pattern
        if 1 <= start_pos <= n - m + 1:
            count[start_pos] += 1

    # если для позиции набралось столько совпадений, сколько частей — значит полное совпадение
    result = []
    for pos in sorted(count):
        if count[pos] == len(parts):
            result.append(pos)

    for p in result:
        print(p)

#
# text = input()
# pattern = input()
# joker = input()
#
# wildcard(text, pattern, joker)

text = "NTAG"
n = 3
patterns = ["TAGT","TAG","T"]
pattern_lengths = {}
# text = "AB"
# n = 2
# patterns = ["AB","A"]
# pattern_lengths = {}

aho = AhoCorasick()

for idx in range(1, n +1):
    # pattern = input.strip()
    # patterns.append(pattern)
    pattern_lengths[idx] = len(patterns[idx-1])
    aho.add_pattern(patterns[idx-1], idx)

matches = aho.search(text,pattern_lengths)
matches.sort()
for position, pattern_index in matches:
    print(position, pattern_index)
print(f"--max_uplink: " + str(aho.max_uplink()))
print(f"--max_sufflink_chain: " + str(aho.max_sufflink()))