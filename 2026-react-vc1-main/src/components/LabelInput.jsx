import { useFormContext } from "react-hook-form";

const LabelInput = ({
  label,
  name,
  placeholder,
  type,
  validationRules,
  ...rest
}) => {
  const {
    register,
    formState: { errors, isSubmitting },
  } = useFormContext();

  const hasError = name in errors;
  return (
    <div className="mb-3">
      <label htmlFor={name} className="block text-[#0A0A0A] mb-1">
        {label}
      </label>
      <input
        {...register(name, validationRules)}
        id={name}
        name={name}
        type={type}
        disabled={isSubmitting}
        className={`w-full rounded-lg card-text bg-[#F3F3F5] px-3 py-1 h-9 text-[14px]  ${
          hasError ? "border border-[#FF4040]" : ""
        }
      `}
        placeholder={placeholder}
        {...rest}
      />
      {hasError && (
        <p className="text-red-500 text-sm mt-1" data-cy="label_input_error">
          {errors[name].message ?? "invalid value"}
        </p>
      )}
    </div>
  );
};

export default LabelInput;
