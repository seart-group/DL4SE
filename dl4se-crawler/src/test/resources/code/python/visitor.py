from typing import Any
from re import split
from ast import get_docstring
from ast import NodeVisitor, ClassDef, FunctionDef
from utils import empty_df


class VoidVisitor(NodeVisitor):
    """
    As the name suggests, this visitor class does not return anything.
    All visited class and function nodes of a target python file are stored in its DataFrame structure.
    """

    def __init__(self):
        self.df = empty_df()

    @staticmethod
    def __normalize_whitespace__(text: str) -> str:
        """
        Normalizes multiple consecutive instances of all whitespace types
        (space, newline and carriage return) into a single space character.
        """
        return " ".join(text.split())

    @staticmethod
    def __valid_name__(name: str) -> bool:
        """
        Checks if a name string does not adhere to blacklist specifications.
        """
        lower = name.lower()
        return lower != "main" and "test" not in lower and not lower.startswith("_")

    def __visit__(self, node, mode: str) -> Any:
        """
        Generic visit function.
        """
        name = node.name
        line = node.lineno
        comment = get_docstring(node)
        if comment:
            comment = split(r"\n\n", comment)[0]
            comment = VoidVisitor.__normalize_whitespace__(comment)
        if VoidVisitor.__valid_name__(name):
            self.df = self.df.append({
                "name": name,
                "line": line,
                "type": mode,
                "comment": comment
            }, ignore_index=True)

    def visit_ClassDef(self, node: ClassDef) -> Any:
        self.__visit__(node, "class")
        for method in [method for method in node.body if isinstance(method, FunctionDef)]:
            self.__visit__(method, "method")

    def visit_FunctionDef(self, node: FunctionDef) -> Any:
        self.__visit__(node, "function")
