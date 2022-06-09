import bootstrapMixin from "@/mixins/bootstrapMixin";

export default {
    mixins: [ bootstrapMixin ],
    methods: {
        returnHomeAndToast(title, message, variant) {
            this.$router.push({ name: "home" }).then(() => {
                this.appendToast(title, message, variant)
            })
        }
    }
}