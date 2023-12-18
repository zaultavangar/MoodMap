import { act, renderHook } from "@testing-library/react-hooks/dom";
import { describe, expect, it } from "vitest";
import { RecoilRoot } from "recoil";
import { useSearch } from "~hooks/useSearch";

describe("useSearch", () => {
  it("should be able to perform a search", async () => {
    const { result } = renderHook(() => useSearch(), {
      wrapper: RecoilRoot,
    });

    await act(async () => {
      await result.current.search("test query");
    });
  });
});
