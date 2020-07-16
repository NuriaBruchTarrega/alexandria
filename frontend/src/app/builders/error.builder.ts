import {HttpErrorResponse} from '@angular/common/http';

export function buildError(error): { message: string, status: string } {
  console.log('Building error', error);
  let message;
  let status;

  if (error instanceof HttpErrorResponse) {
    const {
      status: httpStatus,
      message: httpMessage,
      error: {message: apiMessage}
    } = error;

    message = apiMessage || httpMessage;
    status = httpStatus;
  } else {
    message = error.message;
  }
  return {message, status};
}
