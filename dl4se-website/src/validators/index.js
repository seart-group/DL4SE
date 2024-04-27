import { helpers } from "@vuelidate/validators";

const password = helpers.regex(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\d).{6,}$/);

export { password };
