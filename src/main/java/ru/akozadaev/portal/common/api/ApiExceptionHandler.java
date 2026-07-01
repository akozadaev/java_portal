package ru.akozadaev.portal.common.api;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.akozadaev.portal.application.exception.ApplicationNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

	/**
	 * Преобразует ошибки Bean Validation в стабильный JSON-ответ.
	 *
	 * @param exception ошибка валидации от Spring MVC
	 * @return ответ с деталями валидации по полям
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> details = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> details.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.badRequest().body(new ApiErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Ошибка валидации запроса",
				details));
	}

	/**
	 * Преобразует ошибки входных данных предметной области в стабильный JSON-ответ.
	 *
	 * @param exception ошибка некорректного аргумента
	 * @return ответ с сообщением валидации
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
		return ResponseEntity.badRequest().body(new ApiErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				exception.getMessage(),
				Map.of()));
	}

	/**
	 * Преобразует ошибки преобразования параметров запроса в стабильный JSON-ответ.
	 *
	 * @param exception ошибка преобразования параметра запроса
	 * @return ответ с сообщением о некорректном параметре
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		String parameterName = exception.getName();

		return ResponseEntity.badRequest().body(new ApiErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Некорректное значение параметра: " + parameterName,
				Map.of(parameterName, "Передано значение: " + exception.getValue())));
	}

	/**
	 * Преобразует отсутствие обращения в стабильный JSON-ответ.
	 *
	 * @param exception ошибка отсутствующего обращения
	 * @return ответ с сообщением об отсутствующем ресурсе
	 */
	@ExceptionHandler(ApplicationNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ApplicationNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
				Instant.now(),
				HttpStatus.NOT_FOUND.value(),
				HttpStatus.NOT_FOUND.getReasonPhrase(),
				exception.getMessage(),
				Map.of()));
	}
}
