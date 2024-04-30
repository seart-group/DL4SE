import { helpers } from "@vuelidate/validators";

const uid = helpers.regex(/^[a-zA-Z0-9_-]{3,64}$/);
const password = helpers.regex(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\d).{6,}$/);

export { uid, password };
