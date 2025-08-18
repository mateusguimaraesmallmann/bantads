import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
  selector: '[appCpfValidator]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: CpfValidatorDirective,
      multi: true
    }
  ]
})

export class CpfValidatorDirective implements Validator {

  validate(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null;
    }

    return this.isCpfValid(control.value) ? null : { cpfInvalido: true };
  }

  private isCpfValid(cpfRaw: string): boolean {
    const cpf = cpfRaw.replace(/\D/g, '');

    if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) {
      return false;
    }

    let soma = 0;
    for (let i = 0; i < 9; i++) {
      soma += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let resto = (soma * 10) % 11;
    if (resto === 10) resto = 0;
    if (resto !== parseInt(cpf.charAt(9))) return false;

    soma = 0;
    for (let i = 0; i < 10; i++) {
      soma += parseInt(cpf.charAt(i)) * (11 - i);
    }
    resto = (soma * 10) % 11;
    if (resto === 10) resto = 0;

    return resto === parseInt(cpf.charAt(10));
  }

}