<template>
  <div id="profile" v-if="fetched">
    <h1 class="d-none">Profile</h1>
    <b-container>
      <b-row>
        <b-col class="mb-3 d-flex justify-content-center justify-content-md-start align-items-center">
          <b-icon-identicon width="4em" height="4em" :identifier="user.uid" class="mr-2" />
          <div>
            <h2 class="m-0 user-select-none">{{ user.uid }}</h2>
            <span>Joined: {{ new Date(user.registered).toISOString().slice(0, 10) }}</span>
          </div>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <b-card class="rounded-0" no-body>
            <b-tabs active-nav-item-class="border-md-bottom" align="center" :vertical="$screen.md" card>
              <b-tab title="General" active>
                <b-card-title title-tag="h2" class="d-none">Update information</b-card-title>
                <b-card-sub-title sub-title-tag="h3" sub-title-text-variant="">Username</b-card-sub-title>
                <b-form @submit.stop.prevent="updateUid">
                  <b-form-row>
                    <b-form-group class="col">
                      <b-input
                        type="text"
                        placeholder="New username"
                        v-model.trim="form.uid"
                        :state="!v$.form.uid.$invalid"
                        :spellcheck="false"
                      />
                      <b-form-invalid-feedback v-if="v$.form.uid.$invalid">
                        <b-icon-exclamation-triangle />
                        <span>
                          Must be between 3 and 64 characters long, consisting only of alphanumeric characters, dashes
                          and underscores.
                        </span>
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-form-row>
                  <b-form-row>
                    <b-form-group class="col">
                      <div
                        :class="{
                          'row-gap-3': true,
                          'column-gap-2': true,
                          'd-grid': !$screen.sm,
                          'd-inline-flex': $screen.sm,
                        }"
                      >
                        <b-form-submit :disabled="!canUpdateUid">Save</b-form-submit>
                        <b-button type="button" @click="form.uid = generateUsername()">Random</b-button>
                      </div>
                    </b-form-group>
                  </b-form-row>
                </b-form>
                <hr />
                <b-card-sub-title sub-title-tag="h3" sub-title-text-variant="">Email</b-card-sub-title>
                <b-form @submit.prevent.stop="updateEmail">
                  <b-form-row>
                    <b-form-group class="col">
                      <b-input
                        type="email"
                        placeholder="New email"
                        v-model.trim="form.email"
                        :state="!v$.form.email.$invalid"
                      />
                    </b-form-group>
                  </b-form-row>
                  <b-form-row>
                    <b-form-group class="col">
                      <b-form-submit :disabled="!canUpdateEmail" :block="!$screen.sm">Change Email</b-form-submit>
                    </b-form-group>
                  </b-form-row>
                </b-form>
                <hr />
                <b-card-sub-title sub-title-tag="h3" sub-title-text-variant="">Password</b-card-sub-title>
                <b-form @submit.stop.prevent="updatePassword">
                  <b-form-row>
                    <b-form-group class="col">
                      <b-input type="password" placeholder="New password" value="🗿🗿🗿🗿🗿🗿🗿🗿" disabled />
                    </b-form-group>
                  </b-form-row>
                  <b-form-row>
                    <b-form-group class="col">
                      <b-form-submit :block="!$screen.sm">Change Password</b-form-submit>
                    </b-form-group>
                  </b-form-row>
                </b-form>
                <hr />
                <b-card-sub-title sub-title-tag="h3" sub-title-text-variant="">Organisation</b-card-sub-title>
                <b-form @submit.stop.prevent="updateOrganisation">
                  <b-form-row>
                    <!-- FIXME: We have to do this manually because the inner group div can not be customized -->
                    <div role="group" class="form-group col">
                      <div class="position-relative">
                        <b-form-auto-complete
                          type="text"
                          v-model.trim="form.organisation"
                          :server="organisationsURL"
                          query-param="name"
                          :debounce-time="250"
                          :server-params="{ size: 5 }"
                          :response-mapper="responseMapper"
                          :state="!v$.form.organisation.$invalid"
                        />
                      </div>
                    </div>
                  </b-form-row>
                  <b-form-row>
                    <b-form-group class="col mb-0">
                      <b-form-submit :disabled="!canUpdateOrganisation" :block="!$screen.sm">Save</b-form-submit>
                    </b-form-group>
                  </b-form-row>
                </b-form>
              </b-tab>
            </b-tabs>
          </b-card>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import getRandomName from "namesgenerator";
import useVuelidate from "@vuelidate/core";
import { email, required } from "@vuelidate/validators";
import { uid } from "@/validators";
import routerMixin from "@/mixins/routerMixin";
import organisationsMixin from "@/mixins/organisationsMixin";
import bootstrapMixin from "@/mixins/bootstrapMixin";
import BFormSubmit from "@/components/FormSubmit";
import BIconIdenticon from "@/components/IconIdenticon";
import BFormAutoComplete from "@/components/FormAutoComplete.vue";

export default {
  mixins: [routerMixin, organisationsMixin, bootstrapMixin],
  components: {
    BFormAutoComplete,
    BFormSubmit,
    BIconIdenticon,
  },
  computed: {
    canUpdateUid() {
      const invalid = this.v$.form.uid.$invalid;
      const changed = this.form.uid !== this.user.uid;
      return !invalid && changed;
    },
    canUpdateEmail() {
      const invalid = this.v$.form.email.$invalid;
      const changed = this.form.email !== this.user.email;
      return !invalid && changed;
    },
    canUpdateOrganisation() {
      const invalid = this.v$.form.organisation.$invalid;
      const changed = this.form.organisation !== this.user.organisation;
      return !invalid && changed;
    },
  },
  methods: {
    generateUsername(separator = "_") {
      const name = getRandomName(separator);
      const numbers = Math.floor(Math.random() * 1000);
      return name + separator + numbers;
    },
    responseMapper(json) {
      return json.map((item) => item.name);
    },
    updateUid() {
      const config = { headers: { "Content-Type": "text/plain;charset=UTF-8" } };
      this.$http
        .put("/user/uid", this.form.uid, config)
        .then(() => {
          this.user.uid = this.form.uid;
          this.appendToast("Username updated", "Your username has been successfully updated.", "secondary");
        })
        .catch((err) => {
          switch (err.response.status) {
            case 400: {
              this.appendToast("Form Error", "Invalid form inputs.", "warning");
              break;
            }
            case 409: {
              this.appendToast("Form Error", "Username already in use.", "warning");
              break;
            }
            default: {
              this.appendToast(
                "Server Error",
                "An unexpected server error has occurred. Please try again later.",
                "danger",
              );
            }
          }
        });
    },
    async updateEmail() {
      const config = { headers: { "Content-Type": "text/plain;charset=UTF-8" } };
      this.$http
        .put("/user/email", this.form.email, config)
        .then(() => {
          this.user.email = this.form.email;
          return this.$store.dispatch("logOut");
        })
        .then(() => {
          this.redirectHomeAndToast(
            "Email Change Requested",
            `Your email has been updated.
             We have sent you a verification link.
             Please check the your email for further instructions.`,
            "secondary",
          );
        })
        .catch((err) => {
          switch (err.response.status) {
            case 400: {
              this.appendToast("Form Error", "Invalid form inputs.", "warning");
              break;
            }
            case 409: {
              this.appendToast("Form Error", "Email already in use.", "warning");
              break;
            }
            default: {
              this.appendToast(
                "Server Error",
                "An unexpected server error has occurred. Please try again later.",
                "danger",
              );
            }
          }
        });
    },
    async updatePassword() {
      const payload = { email: this.user.email };
      await this.$http.post("/user/password/forgotten", payload);
      await this.$store.dispatch("logOut");
      this.redirectHomeAndToast(
        "Password Change Requested",
        `We have initiated a password change procedure for your account.
         Please check your email for further instructions.`,
        "secondary",
      );
    },
    updateOrganisation() {
      const config = { headers: { "Content-Type": "text/plain;charset=UTF-8" } };
      this.$http
        .put("/user/organisation", this.form.organisation, config)
        .then(() => {
          this.user.organisation = this.form.organisation;
          this.appendToast("Organisation updated", "Your organisation has been successfully updated.", "secondary");
        })
        .catch((err) => {
          switch (err.response.status) {
            case 400: {
              this.appendToast("Form Error", "Invalid form inputs.", "warning");
              break;
            }
            default: {
              this.appendToast(
                "Server Error",
                "An unexpected server error has occurred. Please try again later.",
                "danger",
              );
            }
          }
        });
    },
  },
  async beforeMount() {
    await this.$http
      .get("/user")
      .then(({ data }) => {
        Object.assign(this.user, data);
        this.form.uid = this.user.uid;
        this.form.email = this.user.email;
        this.form.organisation = this.user.organisation;
        this.fetched = true;
      })
      .catch((err) => {
        switch (err.response.status) {
          case 401: {
            this.$store.dispatch("logOut").then(() => {
              this.appendToast("Login Required", "Your session has expired. Please log in again.", "secondary");
            });
            break;
          }
          default: {
            this.appendToast(
              "Server Error",
              "An unexpected server error has occurred. Please try again later.",
              "danger",
            );
          }
        }
      });
  },
  setup() {
    return {
      v$: useVuelidate(),
    };
  },
  data() {
    return {
      fetched: false,
      form: {
        uid: undefined,
        email: undefined,
        organisation: undefined,
      },
      user: {
        uid: undefined,
        email: undefined,
        verified: undefined,
        enabled: undefined,
        role: undefined,
        registered: undefined,
        organisation: undefined,
      },
    };
  },
  validations() {
    return {
      form: {
        uid: {
          $autoDirty: true,
          required: required,
          uid: uid,
        },
        email: {
          $autoDirty: true,
          required: required,
          email: email,
        },
        organisation: {
          $autoDirty: true,
          required: required,
        },
      },
    };
  },
  head() {
    return {
      title: "Profile",
    };
  },
};
</script>

<style scoped lang="sass" src="@/assets/styles/view/profile.sass" />
