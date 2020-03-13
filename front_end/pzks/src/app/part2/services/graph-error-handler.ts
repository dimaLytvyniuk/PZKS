import { ErrorHandler, Injectable } from '@angular/core';
import { NetworkParsingException } from '../errors/NetworkParsingException';

@Injectable()
export class GraphErrorHandler implements ErrorHandler {
    constructor () { }

    handleError(error) {
        if (error instanceof NetworkParsingException) {
            alert(error.message);
        } else {
            throw error;
        }
    }
}