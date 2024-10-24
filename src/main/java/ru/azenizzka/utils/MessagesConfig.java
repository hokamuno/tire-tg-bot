package ru.azenizzka.app.utils;

public class MessagesConfig {
  public static final String RETURN_COMMAND = "/назад";
  public static final String RECESS_SCHEDULE_COMMAND = "/звонки";
  public static final String LESSON_SCHEDULE_COMMAND = "/расписание";
  public static final String SETTINGS_COMMAND = "/настройки";
  public static final String CHANGE_GROUP_COMMAND = "/сменить_группу";
  public static final String BROADCAST_COMMAND = "/broadcast";
  public static final String SET_ADMIN_COMMAND = "/setadmin";
  public static final String DEL_ADMIN_COMMAND = "/deladmin";

  public static final String INFO_COMMAND = "/info";

  public static final String INFO_HEADER = "Список пользователей. Количество: %d\n\n";

  public static final String BELL_MESSAGE = "✏ *Введите тип звонков*";
  public static final String DAY_MESSAGE = "✏ *Введите день*";
  public static final String CHANGE_GROUP_MESSAGE =
      """
			✏ Введите номер вашей группы.

			Пример: Если ваша группа *МР-232*, то вводите *232*.""";
  public static final String SUCCESS_CHANGE_GROUP_MESSAGE =
      "\uD83D\uDFE2 Вы усппешно сменили группу";
  public static final String NO_LESSONS_MESSAGE = "\uD83E\uDD42 *В этот день у вас нет занятий!*";

  public static final String HELP_MESSAGE =
      "*"
          + LESSON_SCHEDULE_COMMAND
          + "*\n"
          + "Расписание занятий\n\n"
          + "*"
          + RECESS_SCHEDULE_COMMAND
          + "*\n"
          + "Расписание звонков\n\n"
          + "*"
          + SETTINGS_COMMAND
          + "*\n"
          + "Настройки бота";

  public static final String ERROR_TEMPLATE =
      """
			⭕ Что-то пошло не так..
			Текст ошибки: *%s*.

			\uD83D\uDC68\uD83C\uDFFF\u200D\uD83D\uDCBB Обратная связь: @Azenizzka""";

  public static final String NOTIFY_TEMPLATE =
      """
			🔔 Новое уведомление
			*%s*

			\uD83D\uDC68\uD83C\uDFFF\u200D\uD83D\uDCBB Обратная связь: @Azenizzka""";

  public static final String BELL_TYPE_CONVERT_EXCEPTION = "Некорректный тип звонков";
  public static final String DAY_INPUT_EXCEPTION = "Такого дня не существует";
  public static final String UNKNOWN_COMMAND_EXCEPTION = "Такой команды не существует";
  public static final String GROUP_NOT_FOUND_EXCEPTION = "Такой группы не существует";
  public static final String GROUP_NOT_DEFINED_EXCEPTION =
      "Необходимо установить группу в настройках";

  public static final String NULL_BROADCAST_MESSAGE_EXCEPTION = "Необходимо ввести текст";

  public static final String PERSON_NOT_FOUND_EXCEPTION = "Чат с таким ID не найден!";

  public static final String PERSON_ALREADY_ADMIN = "Пользователь уже является администратором";
  public static final String PERSON_ALREADY_NOT_ADMIN = "Пользователь не является администратором";

  public static final String SUCCES_SET_ADMIN = "Вы успешно назначили нового администратора";
  public static final String SUCCES_DEL_ADMIN = "Вы успешно разжаловали администратора";

  public static final String YOU_ARE_ADMIN = "Вы были назначены администратором";
  public static final String YOU_ARE_NOT_ADMIN = "Вы были разжалованы из администраторов";
}
