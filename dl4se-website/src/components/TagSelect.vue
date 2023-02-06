<template>
  <div class="tag-select">
    <input type="file" ref="tags-file" class="d-none" @change="read" />
    <b-form-tags
      :id="id"
      no-outer-focus
      separator=" "
      :placeholder="placeholder"
      v-model="tags"
      :tag-validator="validator"
      class="tag-select-container"
    >
      <template
        v-slot="{
          inputAttrs,
          inputHandlers,
          placeholder,
          tags,
          addTag,
          removeTag,
          isInvalid,
          invalidTagText,
          isDuplicate,
          duplicateTagText
        }"
      >
        <b-input-group class="tag-select-input-group">
          <b-form-input
            v-bind="inputAttrs"
            v-on="inputHandlers"
            :disabled="disabled"
            :placeholder="placeholder"
            class="tag-select-input"
          />
          <b-input-group-append>
            <b-button class="tag-select-btn" :disabled="disabled" @click="addTag()">Add</b-button>
            <b-button class="tag-select-btn" :disabled="disabled" @click="upload">Upload</b-button>
            <b-button class="tag-select-btn" :disabled="disabled" @click="reset">Reset</b-button>
          </b-input-group-append>
        </b-input-group>
        <b-form-invalid-feedback :state="!isInvalid" class="tag-select-feedback-invalid">
          {{ invalidTagText }}
        </b-form-invalid-feedback>
        <b-form-text v-if="isDuplicate" class="tag-select-feedback-text">
          {{ duplicateTagText }}
        </b-form-text>
        <div
          :class="!!tags.length ? 'tag-select-tags-container' : 'tag-select-tags-container-hidden'"
        >
          <b-form-tag
            v-for="tag in tags"
            :key="tag"
            ref="tags-values"
            class="tag-select-tag"
            @remove="removeTag(tag)"
          >
            {{ tag }}
          </b-form-tag>
        </div>
      </template>
    </b-form-tags>
  </div>
</template>

<script>
export default {
  name: "b-tag-select",
  props: {
    id: String,
    value: {
      type: Array,
      required: true
    },
    disabled: Boolean,
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
        return /^\w*$/.test(tag)
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
      this.$refs["tags-file"].click()
    },
    reset() {
      this.tags = []
      this.$refs["tags-file"].value = null
    }
  },
  watch: {
    disabled: {
      handler() {
        this.reset()
      }
    },
    tags: {
      handler() {
        this.$emit("input", this.tags)
      }
    }
  },
  updated() {
    const tags = this.$refs["tags-values"]
    tags
      ?.map((tag) => tag.$el.children[1])
      .filter((button) => !button.hasAttribute("tabindex"))
      .forEach((button) => button.setAttribute("tabindex", "-1"))
  },
  data() {
    return {
      tags: this.value
    }
  }
}
</script>
