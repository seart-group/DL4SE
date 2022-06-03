<template>
  <div class="tag-select">
    <input type="file" ref="tags-file" class="d-none" @change="read">
    <b-input-group>
      <b-form-tags :id="id" class="tag-select-input"
                   :placeholder="placeholder" :separator="[' ']"
                   :tag-validator="validator" v-model="tags"
      />
      <b-input-group-append>
        <b-button type="button" class="tag-select-btn" @click="upload">
          Upload
        </b-button>
        <b-button type="button" class="tag-select-btn" @click="reset">
          Reset
        </b-button>
      </b-input-group-append>
    </b-input-group>
  </div>
</template>

<script>
export default {
  name: "b-tag-select",
  props: {
    id: String,
    value: {
      type: Array[String],
      required: true
    },
    separator: {
      type: String,
      default: "\n"
    },
    placeholder: {
      type: String,
      default: "Your tag..."
    },
    validator: {
      type: Function,
      default(tag) {
        return /^[A-Za-z]{1,20}$/.test(tag)
      }
    }
  },
  methods: {
    read(ev) {
      const file = ev.target.files[0]
      const reader = new FileReader()
      reader.onload = (event) => {
        const contents = event.target.result
        const lines = contents.split(this.separator)
        this.tags = lines.filter(this.validator)
      }
      reader.readAsText(file)
    },
    upload() {
      this.$refs['tags-file'].click()
    },
    reset() {
      this.tags = []
      this.$refs['tags-file'].value = null
    }
  },
  watch: {
    idioms: {
      handler() {
        this.$emit("input", this.tags)
      }
    }
  },
  data() {
    return {
      tags: this.value
    }
  }
}
</script>