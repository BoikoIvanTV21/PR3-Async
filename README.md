# PR3 - Варіант 3
Містить програми Task1WorkStealing; Task1WorkDealing; Task2;

Завдання 1

Work Stealing 100x100 Array :

Execution time: 2 ms

Work Dealing 100x100 Array :

Execution time: 7 ms

Work Stealing ефективніше розподіляє навантаження між потоками при великих числах. Задачі рекурсивно діляться на підзадачі, і коли один потік завершує свою роботу, він може забрати завдання в іншого. Work Dealing має фіксовану кількість потоків що може призводити до нерівномірного навантаження при обробці дуже великих масивів.


Завдання 2

Work Stealing було обрано, оскільки у великих директоріях наперед невідомо де заздалегідь буде більше файлів/субдиректорій для перевірки, де операційна система не дозволятиме зчитувати файли і так далі. Work stealing дозволяє оптимальніше розподілити завдання в порівнянні з Work Dealing.
